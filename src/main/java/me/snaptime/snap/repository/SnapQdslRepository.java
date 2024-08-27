package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.album.domain.Album;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface SnapQdslRepository {

    /*
        자신이 팔로우한 유저의 공개스냅을 최신순으로 10개씩 가져옵니다.
        다음페이지 유무체크를 위해 1개의 스냅을 추가로 가져옵니다.

        pageNum : 페이지 번호
        reqUser : 요청자의 User도메인
    */
    List<Tuple> findSnapPage(Long pageNum, User reqUser);

    /*
        앨범의 썸네일Snap을 조회합니다.
        썸네일은 공개스냅중 제일 최신스냅이 선택되며 공개스냅이 없을 시 null을 반환합니다.

        Album : 썸네일조회할 앨범
    */
    Optional<Snap> findThumnailSnap(Album album);
}
