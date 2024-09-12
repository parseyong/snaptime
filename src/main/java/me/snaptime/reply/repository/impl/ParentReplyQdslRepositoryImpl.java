package me.snaptime.reply.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.repository.ParentReplyQdslRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class ParentReplyQdslRepositoryImpl implements ParentReplyQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findReplyPage(Long snapId, Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.loginId, user.profilePhotoName,user.nickname,
                        parentReply.content,parentReply.parentReplyId,parentReply.lastModifiedDate
                )
                .from(parentReply)
                .join(user).on(parentReply.writer.userId.eq(user.userId))
                .where(parentReply.snap.snapId.eq(snapId))
                .orderBy(parentReply.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_EXIST);

        return tuples;
    }


}
