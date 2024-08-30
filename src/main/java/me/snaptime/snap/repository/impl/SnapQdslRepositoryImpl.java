package me.snaptime.snap.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.album.domain.QAlbum;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapQdslRepository;
import me.snaptime.user.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.snapTag.domain.QSnapTag.snapTag;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class SnapQdslRepositoryImpl implements SnapQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /*
           하나의 쿼리로 모든 정보를 가져오는 로직은 복잡한 join조건과
           여러개의 일대다 관계를 가지는 테이블간 조인이 여러개 이루어지다보니 조인테이블의 양이 증가함에따라
           성능이 오히려 저하되는 모습이 확인되어 두 개의 쿼리로 분리하여 로직을 수행하도록 했습니다.
    */
    @Override
    public List<Tuple> findSnapPage(Long pageNum, User reqUser) {

        Pageable pageable= PageRequest.of((int) (pageNum-1),10);

        // 내가 팔로우한 유저의 id를 가져오는 쿼리
        List<Long> followUserIds = jpaQueryFactory.select( user.userId ).distinct()
                .from(user)
                .join(friend).on(friend.receiver.userId.eq(user.userId))
                .where(friend.sender.userId.eq(reqUser.getUserId()))
                .fetch();
        
        // 나의 스냅도 커뮤니티에 포함되기 위해 나의 id추가
        followUserIds.add(reqUser.getUserId());

        // 팔로우유저의 스냅을 최신순으로 조회
        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.loginId, user.profilePhotoName, user.nickname,
                        snap.snapId, snap.createdDate, snap.lastModifiedDate, snap.oneLineJournal, snap.fileName
                ).distinct()
                .from(user)
                .join(snap).on(snap.writer.userId.eq(user.userId))
                .where(user.userId.in(followUserIds).and(snap.isPrivate.isFalse()))
                .orderBy(snap.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //다음 페이지 유무체크를 위해 +1을 합니다.
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_EXIST);

        return tuples;
    }

    @Override
    public List<Snap> findThumnailSnaps(Album album, Long thumnailCnt) {

        QAlbum qAlbum = new QAlbum("album");

        List<Snap> snaps = jpaQueryFactory.select(snap)
                .from(snap)
                .join(qAlbum).on(snap.album.albumId.eq(album.getAlbumId()))
                .where(snap.isPrivate.isFalse())
                .orderBy(snap.createdDate.desc())
                .limit(thumnailCnt)
                .fetch();

        return snaps;
    }

    @Override
    public List<Snap> findTagedSnaps(User targetUser) {

        return jpaQueryFactory.select( snap )
                .from(snap)
                .join(snapTag).on(snap.snapId.eq(snapTag.snap.snapId))
                .where(snapTag.tagUser.userId.eq(targetUser.getUserId()).and(snap.isPrivate.isFalse()))
                .orderBy(snap.createdDate.desc())
                .fetch();
    }
}
