package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
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
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;
import me.snaptime.snap.dto.res.SnapFindResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snap.service.SnapService;
import me.snaptime.snapLike.service.SnapLikeService;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    public void addSnap(String reqLoginId, SnapAddReqDto snapAddReqDto) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        String fileName = addPhoto(reqUser, snapAddReqDto.multipartFile(), snapAddReqDto.isPrivate());

        Snap snap = Snap.builder()
                .user(reqUser)
                .isPrivate(snapAddReqDto.isPrivate())
                .oneLineJournal(snapAddReqDto.oneLineJournal())
                .fileName(fileName)
                .fileType(snapAddReqDto.multipartFile().getContentType())
                .album(albumService.findAlbumForSnapAdd(reqUser, snapAddReqDto.albumId()))
                .build();

        snapRepository.save(snap);

        // tagUserLoginIds가 파라미터로 주어졌을 경우 태그에 추가
        if (snapAddReqDto.tagUserLoginIds() != null) {
            snapTagService.addTagUser(snapAddReqDto.tagUserLoginIds(), snap);
        }
    }

    @Override
    public SnapFindDetailResDto findSnapDetail(String reqLoginId, Long snapId) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        if(snap.isPrivate()) {
            isMySnap(reqUser,snap);
        }

        String profilePhotoURL = urlComponent.makePhotoURL(reqUser.getProfilePhotoName(), false);
        String snapPhotoURL =  urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate());

        return SnapFindDetailResDto.toDto(snap,
                profilePhotoURL,
                snapPhotoURL,
                snapTagService.findTagUsers(snap.getSnapId()),
                snapLikeService.findSnapLikeCnt(snap.getSnapId()),
                snapLikeService.isLikedSnap(snap.getSnapId(), reqLoginId)
        );
    }

    @Override
    @Transactional
    public void updateSnap(String reqLoginId, Long snapId, SnapUpdateReqDto snapUpdateReqDto) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        isMySnap(reqUser,snap);

        if(snapUpdateReqDto.multipartFile() != null){

            // 기존 사진 삭제
            photoComponent.deletePhoto(snap.getFileName());
            String fileName = addPhoto(reqUser, snapUpdateReqDto.multipartFile(), snap.isPrivate());
            snap.updateFileName(fileName);
            snap.updateFileType(snapUpdateReqDto.multipartFile().getContentType());
        }
        
        snap.updateOneLineJournal(snapUpdateReqDto.oneLineJournal());

        snapRepository.save(snap);
        // 태그정보 수정
        snapTagService.updateTagUsers(snapUpdateReqDto.tagUserLoginIds(), snap);
    }

    @Override
    @Transactional
    public void updateSnapVisibility(String reqLoginId, Long snapId, boolean isPrivate) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        // 이미 공개스냅이거나 비공개스냅이면
        if (snap.isPrivate() == isPrivate) {
            throw new CustomException(ExceptionCode.ALREADY_SNAP_VISIBILITY);
        }
        isMySnap(reqUser,snap);

        if(isPrivate){
            // 암호화하여 저장
            byte[] encryptedPhoto = cipherComponent.encryptData(
                    reqUser.getSecretKey(),
                    photoComponent.findPhoto(snap.getFileName())
            );
            photoComponent.updatePhotoVisibility(snap.getFileName(), encryptedPhoto);
        }
        else{
            // 복호화하여 저장
            byte[] decryptedPhoto = cipherComponent.decryptData(
                    reqUser.getSecretKey(),
                    photoComponent.findPhoto(snap.getFileName())
            );
            photoComponent.updatePhotoVisibility(snap.getFileName(), decryptedPhoto);
        }

        snap.updateIsPrivate(isPrivate);
        snapRepository.save(snap);
    }

    @Override
    @Transactional
    public void updateSnapPosition(String reqLoginId, Long snapId, Long albumId) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        albumService.isMyAlbum(reqUser, album);
        isMySnap(reqUser,snap);

        snap.updateAlbum(album);
        snapRepository.save(snap);
    }

    @Override
    @Transactional
    public void deleteSnap(String reqLoginId, Long snapId) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        isMySnap(reqUser,snap);

        photoComponent.deletePhoto(snap.getFileName());
        snapRepository.delete(snap);
    }

    @Override
    @Transactional(readOnly = true)
    public SnapFindAllInAlbumResDto findAllSnapInAlbum(String reqLoginId, Long albumId) {

        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        albumService.isMyAlbum(reqUser, album);
        List<Snap> snaps = snapRepository.findAllByAlbum( album );

        List<SnapFindResDto> snapFindResDtos = snaps.stream().map(snap -> {
            String snapPhotoURL = urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate());
            return SnapFindResDto.toDto(snap, snapPhotoURL);
        }).collect(Collectors.toList());

        return SnapFindAllInAlbumResDto.toDto(snapFindResDtos,album);

    }

    private String addPhoto(User reqUser, MultipartFile multipartFile, boolean isPrivate) {
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

    private void isMySnap(User reqUser, Snap snap){
        if(snap.getUser().getUserId() != reqUser.getUserId())
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);
    }
}
