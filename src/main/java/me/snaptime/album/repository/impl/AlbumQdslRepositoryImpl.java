package me.snaptime.album.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.album.repository.AlbumQdslRepository;
import me.snaptime.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static me.snaptime.album.domain.QAlbum.album;

@Repository
@RequiredArgsConstructor
public class AlbumQdslRepositoryImpl implements AlbumQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Value(value = "${basicAlbumName}")
    private String basicAlbumName;

    @Override
    public Optional<Album> findBasicAlbumByUser(User user) {

        return Optional.ofNullable(jpaQueryFactory.select(album)
                .from(album)
                .join(album).on(album.user.userId.eq(user.getUserId()))
                .where(album.albumName.eq(basicAlbumName))
                .orderBy(album.createdDate.asc())
                .fetchFirst());
    }
}
