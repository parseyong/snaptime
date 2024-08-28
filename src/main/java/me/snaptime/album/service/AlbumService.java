package me.snaptime.album.service;

import me.snaptime.album.domain.Album;
import me.snaptime.album.dto.res.AlbumFindResDto;
import me.snaptime.user.domain.User;

import java.util.List;

public interface AlbumService {

    /*
        썸네일 URL이 포함된 앨범리스트를 조회합니다.
        썸네일은 공개스냅중 제일 최신스냅이 선택됩니다.
        앨범id, albumName, 썸네일URL이 포함된 DTO리스트를 반환합니다.
        앨범에 공개스냅이 없을경우 썸네일URL은 NULL입니다.

        reqLoginId : 앨범을 조회할 유저의 loginId
    */
    List<AlbumFindResDto> findAllAlbumsWithThumnail(String reqLoginId);

    /*
        썸네일이 미포함된 앨범리스트를 조회합니다.
        앨범id, albumName이 포함된 DTO리스트를 반환합니다.

        reqLoginId : 앨범을 조회할 유저의 loginId
    */
    List<AlbumFindResDto> findAllAlbums(String reqLoginId);

    /*
        새 앨범을 생성합니다.

        reqLoginId : 앨범을 생성할 유저의 loginId
        albumName  : 앨범의 이름
    */
    void addAlbum(String reqLoginId, String albumName);

    /*
        앨범이름을 수정합니다. 기본앨범은 수정할 수 없습니다.

        reqLoginId   : 앨범을 수정할 유저의 loginId, 자신의 앨범이 아니라면 예외가 반환됩니다.
        albumId      : 수정할 앨범 Id
        newAlbumName : 새로운 앨범이름
    */
    void updateAlbumName(String reqLoginId, Long albumId, String newAlbumName);

    /*
        앨범을 삭제합니다. 앨범안에 있는 스냅들은 자동으로 기본앨범으로 이동됩니다.
        기본앨범은 삭제할 수 없습니다.

        reqLoginId   : 앨범을 삭제할 유저의 loginId, 자신의 앨범이 아니라면 예외가 반환됩니다.
        albumId      : 삭제할 앨범 Id
    */
    void deleteAlbum(String reqLoginId, Long albumId);

    /*
        기본앨범을 생성합니다. 회원가입 시 무조건 호출되어야하는 메소드입니다.

        user : 기본앨범을 생성할 유저
    */
    void addBasicAlbum(User user);

    /*
        유저의 기본앨범을 가져옵니다. 기본앨범이 없다면 예외를 반환합니다.
        기본Album을 반환합니다.

        user : 기본앨범을 가져올 유저
    */
    Album findBasicAlbum(User user);

    /*
        앨범이 자신이 만든 앨범인지 확인합니다.
        자신소유의 앨범이 아니라면 예외를 반환합니다.

        user  : 비교할 대상 user
        album : 비교할 대상 album
    */
    void isMyAlbum(User user, Album album);

    /*
        스냅저장 시 스냅이 저장되는 스냅을 가져옵니다.
        albumId가 null인 경우 basicAlbum이 선택되며
        albumId가 자신이 만든 앨범이 아닌경우 예외를 반환합니다.

        reqUser : 요청유저의 loginId
        albumId : 저장할 앨범 id
    */
    Album findAlbumForSnapAdd(User reqUser, Long albumId);
    
    /*
        앨범의 썸네일사진 URL을 가져옵니다.
        
        album : 썸네일 조회할 앨범
    */
    String findAlbumThumbnailPhotoURL(Album album);
}
