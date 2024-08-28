package me.snaptime.friend.repository;

import com.querydsl.core.Tuple;
import me.snaptime.friend.enums.FriendSearchType;
import me.snaptime.user.domain.User;

import java.util.List;

public interface FriendQdslRepository {

    /*
        targetUser의 친구목록을 조회합니다.

         targetUser    : 친구목록을 조회할 유저
         searchType    : 팔로워, 팔로잉중 어느것을 조회할 지 입력해주세요.
         pageNum       : 친구목록 페이지번호
         searchKeyword : 검색어. 없으면 null
    */
    List<Tuple> findFriendPage(User targetUser, FriendSearchType searchType, Long pageNum , String searchKeyword);
}
