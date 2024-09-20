package me.snaptime.album.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.album.dto.req.AlbumAddReqDto;
import me.snaptime.album.dto.req.AlbumUpdateReqDto;
import me.snaptime.album.dto.res.AlbumFindResDto;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.album.service.AlbumService;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumServiceImpl implements AlbumService {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;

    @Value(value = "${basicAlbumName}")
    private String basicAlbumName;

    @Override
    public List<AlbumFindResDto> findAllAlbumsWithThumnail(String reqEmail, String targetUserEmail, Long thumnailCnt) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        User targetUser = userRepository.findByEmail(targetUserEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<Album> albums = albumRepository.findAllByUser(targetUser);
        boolean isMine = targetUser.getUserId() == reqUser.getUserId();

        return albums.stream().map(album -> {
            // 각 앨범의 썸네일 스냅조회
            List<Snap> thumnailSnaps = snapRepository.findThumnailSnaps(album, thumnailCnt, isMine);

            // 썸네일스냅의 사진URL생성
            List<String> thumbnailPhotoURLs = thumnailSnaps.stream().map(thumnailSnap -> {
                return urlComponent.makePhotoURL(thumnailSnap.getFileName(),false);
            }).collect(Collectors.toList());

            return AlbumFindResDto.toDto(album,thumbnailPhotoURLs);
        }).collect(Collectors.toList());
    }

    @Override
    public List<AlbumFindResDto> findAllMyAlbums(String reqEmail) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return albumRepository.findAllByUser(reqUser).stream().map(album ->
                        AlbumFindResDto.toDto(album,null)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addAlbum(String reqEmail, AlbumAddReqDto albumAddReqDto) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        albumRepository.save(
                Album.builder()
                .albumName(albumAddReqDto.albumName())
                .user(reqUser)
                .build());
    }

    @Override
    @Transactional
    public void updateAlbumName(String reqEmail, Long albumId, AlbumUpdateReqDto albumUpdateReqDto) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        checkMyAlbum(reqUser,album);
        Album basicAlbum = findBasicAlbum(reqUser);

        // 수정하려는 앨범이 기본앨범이면 예외반환
        if(basicAlbum.getAlbumId().equals(albumId)) {
            throw new CustomException(ExceptionCode.CAN_NOT_BE_MODIFIED_OR_DELETED_BASIC_ALBUM);
        }

        album.updateAlbumName(albumUpdateReqDto.newAlbumName());
    }

    @Override
    @Transactional
    public void deleteAlbum(String reqEmail, Long albumId) {

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));

        checkMyAlbum(reqUser, album);
        Album basicAlbum = findBasicAlbum(reqUser);

        // 삭제하려는 앨범이 기본앨범이면 예외반환
        if(basicAlbum.getAlbumId().equals(albumId)) {
            throw new CustomException(ExceptionCode.CAN_NOT_BE_MODIFIED_OR_DELETED_BASIC_ALBUM);
        }

        moveSnapToBasicAlbumForAlbumDelete(album, basicAlbum);
        albumRepository.delete(album);
    }

    @Override
    public Album findBasicAlbum(User user) {

        // 해당 유저의 모든 앨범을 조회
        Optional<Album> basicAlbumOptional = albumRepository.findBasicAlbumByUser(user);

        if(basicAlbumOptional.isEmpty())
            throw new CustomException(ExceptionCode.BASIC_ALBUM_NOT_EXIST);

        return basicAlbumOptional.get();
    }

    @Override
    public void addBasicAlbum(User user) {

        Album basicAlbum = Album.builder()
                .user(user)
                .albumName(basicAlbumName)
                .build();

        albumRepository.save(basicAlbum);
    }

    @Override
    public void checkMyAlbum(User reqUser, Album album) {

        if( album.getUser().getUserId() != reqUser.getUserId() ){
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALBUM);
        }
    }

    @Override
    public Album findAlbumForSnapAdd(User reqUser, Long albumId) {

        if(albumId == null){
            return findBasicAlbum(reqUser);
        }
        else {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
            checkMyAlbum(reqUser,album);
            return album;
        }
    }

    private void moveSnapToBasicAlbumForAlbumDelete(Album deletedAlbum, Album basicAlbum){

        List<Snap> snaps = snapRepository.findAllByAlbum(deletedAlbum);
        snaps.forEach(snap -> snap.updateAlbum(basicAlbum));

        snapRepository.saveAll(snaps);
    }

}
