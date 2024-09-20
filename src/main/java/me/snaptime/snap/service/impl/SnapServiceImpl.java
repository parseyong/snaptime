package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.album.service.AlbumService;
import me.snaptime.component.CipherComponent;
import me.snaptime.component.PhotoComponent;
import me.snaptime.component.UrlComponent;
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
import java.util.Objects;
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
    public void addSnap(String reqEmail, SnapAddReqDto snapAddReqDto, MultipartFile multipartFile) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        String fileName = addPhoto(reqUser, multipartFile, snapAddReqDto.isPrivate());

        Snap snap = Snap.builder()
                .writer(reqUser)
                .isPrivate(snapAddReqDto.isPrivate())
                .oneLineJournal(snapAddReqDto.oneLineJournal())
                .fileName(fileName)
                .fileType(multipartFile.getContentType())
                .album(albumService.findAlbumForSnapAdd(reqUser, snapAddReqDto.albumId()))
                .build();

        snapRepository.save(snap);

        // tagUserLoginIds가 파라미터로 주어졌을 경우 태그에 추가
        if (snapAddReqDto.tagUserEmails() != null) {
            snapTagService.addTagUser(snapAddReqDto.tagUserEmails(), snap);
        }
    }

    @Override
    public SnapFindDetailResDto findSnapDetail(String reqEmail, Long snapId) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        if(snap.isPrivate() && !isMySnap(reqUser, snap)) {
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);
        }

        String profilePhotoURL = urlComponent.makePhotoURL(reqUser.getProfilePhotoName(), false);
        String snapPhotoURL =  urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate());

        return SnapFindDetailResDto.toDto(snap,
                profilePhotoURL,
                snapPhotoURL,
                snapTagService.findTagUsers(snap.getSnapId()),
                snapLikeService.findSnapLikeCnt(snap.getSnapId()),
                snapLikeService.isLikedSnap(snap.getSnapId(), reqEmail)
        );
    }

    @Override
    @Transactional
    public void updateSnap(String reqEmail, Long snapId, SnapUpdateReqDto snapUpdateReqDto, MultipartFile multipartFile) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        if(!isMySnap(reqUser,snap))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);

        if(multipartFile != null){

            // 기존 사진 삭제
            photoComponent.deletePhoto(snap.getFileName());
            String fileName = addPhoto(reqUser, multipartFile, snap.isPrivate());
            snap.updateFileName(fileName);
            snap.updateFileType(multipartFile.getContentType());
        }
        
        snap.updateOneLineJournal(snapUpdateReqDto.oneLineJournal());

        snapRepository.save(snap);

        // 태그정보 수정
        snapTagService.updateTagUsers(snapUpdateReqDto.tagUserEmails(), snap);
    }

    @Override
    @Transactional
    public void updateSnapVisibility(String reqEmail, Long snapId, boolean isPrivate) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        // 이미 공개스냅이거나 비공개스냅이면
        if (snap.isPrivate() == isPrivate) {
            throw new CustomException(ExceptionCode.ALREADY_SNAP_VISIBILITY);
        }

        if(!isMySnap(reqUser, snap))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);

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
    public void updateSnapPosition(String reqEmail, Long snapId, Long albumId) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        albumService.checkMyAlbum(reqUser, album);
        if(!isMySnap(reqUser,snap))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);

        snap.updateAlbum(album);
        snapRepository.save(snap);
    }

    @Override
    @Transactional
    public void deleteSnap(String reqEmail, Long snapId) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        if(!isMySnap(reqUser,snap))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_SNAP);

        photoComponent.deletePhoto(snap.getFileName());
        snapRepository.delete(snap);
    }

    @Override
    @Transactional(readOnly = true)
    public SnapFindAllInAlbumResDto findAllSnapInAlbum(String reqEmail, Long albumId) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        List<Snap> snaps = snapRepository.findAllByAlbum( album );

        List<SnapFindResDto> snapFindResDtos = snaps.stream()
                .map(snap -> {
                    if(snap.isPrivate() && !isMySnap(reqUser,snap))
                        return null;

                    String snapPhotoURL = urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate());
                    return SnapFindResDto.toDto(snap, snapPhotoURL);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

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

    private boolean isMySnap(User reqUser, Snap snap){

        if(snap.getWriter().getUserId() != reqUser.getUserId())
            return false;

        return true;
    }
}
