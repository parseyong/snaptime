package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.album.domain.Album;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface SnapPagingRepository {

    /*
        10개의 스냅 + 다음페이지 유무확인을 위한 1개의 스냅을 반환합니다.
    */
    List<Tuple> findSnapPage(Long pageNum, User reqUser);

    Optional<Snap> findThumnailSnap(Album album);
}
