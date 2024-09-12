package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.album.domain.Album;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;

import java.util.List;

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
        썸네일은 공개스냅중 제일 최신스냅이 선택됩니다. 자신의 앨범리스트조회 시 비공개 스냅이 썸네일이 될 수 있으며
        다른사람의 리스트 조회 시 공개스냅만 썸네일로 지정됩니다. 공개스냅이 없을 시 빈 리스트를 반환합니다.

        Album       : 썸네일조회할 앨범
        thumnailCnt : 썸네일 개수
        isMine      : 자신의 앨범을 조회하는 지 여부
    */
    List<Snap> findThumnailSnaps(Album album, Long thumnailCnt, boolean isMine);

    /*
        유저가 태그된 스냅리스트를 조회합니다.

        targetUser : 대상이 되는 유저
    */
    List<Snap> findTagedSnaps(User targetUser);
}
