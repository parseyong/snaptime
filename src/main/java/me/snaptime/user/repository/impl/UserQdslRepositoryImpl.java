package me.snaptime.user.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.repository.UserQdslRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQdslRepositoryImpl implements UserQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> searchUserPaging(String searchKeyword, Long pageNum){
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.userId, user.loginId, user.profilePhotoName, user.nickname
                )
                .from(user)
                .where(user.nickname.startsWith(searchKeyword).or(user.loginId.startsWith(searchKeyword)))
                .orderBy(user.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_EXIST);

        return  tuples;
    }
}