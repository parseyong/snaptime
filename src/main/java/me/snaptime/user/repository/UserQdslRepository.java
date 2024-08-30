package me.snaptime.user.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface UserQdslRepository {

    /*
        유저를 키워드로 검색합니다.
        유저의 닉네임이나 loginId중 searchKeyword로 시작하는 유저를 조회합니다.

        searchKeyword : 검색 키워드
        pageNum       : 페이지번호
    */
    List<Tuple> searchUserPaging(String searchKeyword, Long pageNum);
}
