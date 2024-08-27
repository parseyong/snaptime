package me.snaptime.album.repository;

import me.snaptime.album.domain.Album;
import me.snaptime.user.domain.User;

import java.util.Optional;

public interface AlbumQdslRepository {

    /*
        user의 기본앨범을 조회합니다.
        Album을 Optional로 감싸서 보내줍니다.

        reqUser : 기본앨범을 조회할 유저
    */
    Optional<Album> findBasicAlbumByUser(User reqUser);
}
