package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.album.domain.Album;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.album.service.AlbumService;
import me.snaptime.component.cipher.CipherComponent;
import me.snaptime.component.file.PhotoComponent;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.dto.req.SnapAddReqDto;
import me.snaptime.snap.dto.req.SnapUpdateReqDto;
import me.snaptime.snap.dto.res.PhotoPathResDto;
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;
import me.snaptime.snap.dto.res.SnapFindResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snapLike.service.SnapLikeService;
import me.snaptime.snapTag.dto.res.TagUserFindResDto;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.CipherUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapServiceImpl implements SnapService {

    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PhotoComponent photoComponent;
    private final CipherComponent cipherComponent;
    private final UrlComponent urlComponent;
    private final SnapTagService snapTagService;
    private final AlbumService albumService;
    private final SnapLikeService snapLikeService;

    @Override
    public void addSnap(String reqLoginId, SnapAddReqDto snapAddReqDto) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        PhotoPathResDto photoPathResDto = addPhoto(reqUser, snapAddReqDto.multipartFile(), snapAddReqDto.isPrivate());

        Snap snap = Snap.builder()
                .user(reqUser)
                .isPrivate(snapAddReqDto.isPrivate())
                .oneLineJournal(snapAddReqDto.oneLineJournal())
                .fileName(photoPathResDto.fileName())
                .filePath(photoPathResDto.filePath())
                .fileType(snapAddReqDto.multipartFile().getContentType())
                .album(albumService.findAlbumForSnapAdd(reqUser,snapAddReqDto.albumId()))
                .build();

        // tagUserLoginIds가 파라미터로 주어졌을 경우 태그에 추가
        if (snapAddReqDto.tagUserLoginIds() != null) {
            snapTagService.addTagUser(snapAddReqDto.tagUserLoginIds(), snap);
        }

        snapRepository.save(snap);
    }

    @Override
    public SnapFindDetailResDto findSnap(Long id, String uId) {
        Snap foundSnap = snapRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        if(foundSnap.isPrivate()) {
            User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
            // Snap이 비공개라면, 요청한 유저와 스냅의 ID가 일치하는지 확인한다.
            if (!Objects.equals(foundUser.getUserId(), foundSnap.getUser().getUserId())) {
                throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);
            }
        }
        String snapPhotoUrl = urlComponent.makePhotoURL(foundSnap.getFileName(), foundSnap.isPrivate());
        String profilePhotoUrl = urlComponent.makePhotoURL(foundSnap.getFileName(),false);
        List<TagUserFindResDto> tagUserFindResDtos = snapTagService.findTagUsers(foundSnap.getSnapId());
        Long likeCnt = snapLikeService.findSnapLikeCnt(foundSnap.getSnapId());
        boolean isLikedSnap = snapLikeService.isLikedSnap(foundSnap.getSnapId(), uId);
        return SnapFindDetailResDto.toDto(foundSnap, profilePhotoUrl, snapPhotoUrl, tagUserFindResDtos, likeCnt, isLikedSnap);
    }

    @Override
    public Long modifySnap(Long snapId, SnapUpdateReqDto snapUpdateReqDto, String userUid, List<String> tagUserLoginIds, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        User foundUser = userRepository.findByLoginId(userUid)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 수정하려는 유저와 수정되려는 스냅의 저자가 일치하는지 확인한다.
        if (!foundSnap.getUser().getUserId().equals(foundUser.getUserId())) {
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);
        }

        // 이미지 수정
        if (null != snapUpdateReqDto.multipartFile()) {
            try {
                byte[] foundPhotoByte = snapUpdateReqDto.multipartFile().getInputStream().readAllBytes();
                if (isPrivate) {
                    // 사용자가 이미지 수정까지 요구했고, 암호화까지 원한다면
                    // getEncryption을 통해 사용자의 암호화키를 가져온다. (없으면 null이다.)
                    SecretKey secretKey = foundUser.getSecretKey();

                    byte[] encryptedByte = cipherComponent.encryptData(secretKey, foundPhotoByte);
                    photoComponent.updatePhoto(foundSnap.getFilePath(), encryptedByte);

                    // Snap의 암호화 상태를 활성으로 변경한다.
                    foundSnap.updateIsPrivate(true);
                    // 태그 목록을 삭제한다.
                    snapTagService.deleteAllTagUser(foundSnap);
                } else {
                    // 사용자가 이미지 수정을 요구하였으나, 암호화를 원하지 않는다면
                    // fileComponent를 통해 원래 경로에 사진을 저장한다.
                    photoComponent.updatePhoto(foundSnap.getFilePath(), foundPhotoByte);
                    // Snap의 암호화 상태를 비활성화로 변경한다.
                    foundSnap.updateIsPrivate(false);

                    if (tagUserLoginIds != null) {
                        snapTagService.modifyTagUser(tagUserLoginIds, foundSnap);
                    }
                }
            } catch (IOException e) {
                log.info(e.toString());
            }
        }

        Snap snap = snapRepository.save(foundSnap);
        return snap.getSnapId();
    }

    @Override
    public void changeVisibility(Long snapId, String userUid, boolean isPrivate) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(userUid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 설정되어 있는 값하고 똑같다면
        if (foundSnap.isPrivate() == isPrivate) {
            // 예외를 발생시킨다.
            throw new CustomException(ExceptionCode.ALREADY_SNAP_VISIBILITY);
        }

        // 저장된 filePath 경로로 부터 파일을 가져온다.
        byte[] foundPhotoByte = photoComponent.findPhoto(foundSnap.getFileName());
        if(isPrivate) {
            // public -> private (암호화)
            SecretKey secretKey = foundUser.getSecretKey();
            byte[] encryptedByte = cipherComponent.encryptData(secretKey, foundPhotoByte);
            photoComponent.updatePhoto(foundSnap.getFilePath(), encryptedByte);
            // 태그 목록을 삭제한다.
            snapTagService.deleteAllTagUser(foundSnap);
        } else {
            // private -> public (복호화)
            SecretKey secretKey = foundUser.getSecretKey();
            byte[] decryptedByte = cipherComponent.decryptData(secretKey, foundPhotoByte);
            photoComponent.updatePhoto(foundSnap.getFilePath(), decryptedByte);
        }
        foundSnap.updateIsPrivate(isPrivate);

        snapRepository.save(foundSnap);

    }

    @Override
    public void deleteSnap(Long snapId, String uId) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 삭제를 요청한 사용자가 Snap를 만든 사용자인지 확인한다.
        if (!Objects.equals(foundSnap.getUser().getUserId(), foundUser.getUserId())) {
            // 다르다면 에러를 던진다.
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);
        }
        String fileName = foundSnap.getFileName();
        // 저장소에 보관되어있는 사진을 삭제한다.
        photoComponent.deletePhoto(fileName);
        // DB에서 스냅을 삭제한다.
        snapRepository.delete(foundSnap);
    }

    @Override
    public byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted) {

        byte[] photoData = photoComponent.findPhoto(fileName);

        if (isEncrypted) {
            User user = userRepository.findByLoginId(uId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
            try {
                return CipherUtil.decryptData(photoData, user.getSecretKey());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new CustomException(ExceptionCode.DECRYPT_FAIL);
            }
        }
        return photoData;
    }

    @Override
    public void relocateSnap(Long snapId, Long albumId, String uId) {
        Snap foundSnap = snapRepository.findById(snapId).orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        Album foundAlbum = albumRepository.findById(albumId).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        // 찾은 Snap의 소유자가 요청자와 일치하고, 새로 옮길 앨범의 소유자가 요청자와 일치한다면
        if (Objects.equals(foundSnap.getUser().getUserId(), foundUser.getUserId()) && Objects.equals(foundSnap.getAlbum().getUser().getUserId(), foundUser.getUserId())) {
            // 새로 연관관계를 맺어주고 DB에 반영한다.
            foundSnap.updateAlbum(foundAlbum);
            snapRepository.save(foundSnap);
        } else {
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALBUM);
        }
    }

    private PhotoPathResDto addPhoto(User reqUser, MultipartFile multipartFile, boolean isPrivate) {
        try {
            if (isPrivate) {
                SecretKey secretKey = reqUser.getSecretKey();
                byte[] encryptedFile = cipherComponent.encryptData(secretKey, multipartFile.getInputStream().readAllBytes());
                return photoComponent.addPhoto(multipartFile.getOriginalFilename(), encryptedFile);
            }
            else {
                return photoComponent.addPhoto(multipartFile.getOriginalFilename(), multipartFile.getInputStream().readAllBytes());
            }

        } catch (CustomException customException) {
            throw customException;
        } catch (IOException ioException){
            throw new CustomException(ExceptionCode.PHOTO_FIND_FAIL);
        }
    }

    private void addSnapInAlbum(Snap snap, Long albumId) {

        if(albumId == null){

        }
        else{

        }
        Album foundAlbum = albumRepository.findById(albumId).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        snap.updateAlbum(foundAlbum);
        snapRepository.save(snap);
    }

    private void processSnapForNonClassification(Snap snap, User user){

        Album basicAlbum = albumService.findBasicAlbum(user);
        addSnapInAlbum(snap, basicAlbum.getAlbumId());
    }

    @Override
    @Transactional(readOnly = true)
    public SnapFindAllInAlbumResDto findAllSnapInAlbum(String uId, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Snap> foundSnaps = snapRepository.findAllByAlbum( foundAlbum );

        try {
            Album album = albumRepository.findById(album_id)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
            albumService.isMyAlbum(foundUser, album);
        } catch (CustomException e) {

            /*
             * 유저가 없거나, 권한이 없으면 공개인 것만 유저에게 반환한다.
             * */
            return SnapFindAllInAlbumResDto.builder()
                    .albumId(foundAlbum.getAlbumId())
                    .albumName(foundAlbum.getAlbumName())
                    .snapFindResDtos(foundSnaps.stream()
                            .sorted(Comparator.comparing(Snap::getSnapId).reversed())
                            .filter( snap -> !snap.isPrivate())
                            .map(snap ->
                                    SnapFindResDto.entityToResDto(
                                            snap,
                                            urlComponent.makePhotoURL(snap.getFileName(), false),
                                            urlComponent.makePhotoURL(snap.getUser().getProfilePhotoName(),false)
                                    )
                            )
                            .collect(Collectors.toList()))
                    .build();
        }

        return SnapFindAllInAlbumResDto.builder()
                .albumId(foundAlbum.getAlbumId())
                .albumName(foundAlbum.getAlbumName())
                .snapFindResDtos(foundSnaps.stream()
                        .sorted(Comparator.comparing(Snap::getSnapId).reversed())
                        .map(snap ->
                                SnapFindResDto.entityToResDto(
                                        snap,
                                        urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate()),
                                        urlComponent.makePhotoURL(snap.getUser().getProfilePhotoName(),false)
                                )
                        )
                        .collect(Collectors.toList()))
                .build();

    }
}
