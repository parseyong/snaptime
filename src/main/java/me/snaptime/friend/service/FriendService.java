package me.snaptime.friend.service;

import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.dto.res.FriendFindPagingResDto;
import me.snaptime.friend.enums.FriendSearchType;
import me.snaptime.user.domain.User;

public interface FriendService {

    /*
        팔로우요청을 보냅니다.(sender(요청자)의 팔로잉 +1, receiver의 팔로워 +1)
        자기자신에게 팔로우요청을 보내거나 이미 팔로우관계인 유저에게 팔로우요청을 보내려할 경우
        예외를 반환합니다.

        senderEmail   : 팔로우요청을 보내는 유저의 email
        receiverEmail : 팔로우요청을 받는 유저의 email
    */
    void sendFollow(String senderEmail, String receiverEmail);

    /*
        언팔로우를 합니다.
        (reqEmail(삭제자)의 팔로잉 -1, deletedUserEmail의 팔로워 -1)

        reqEmail         : 요청자(언팔을 하려는 유저)의 email
        deletedUserEmail : 언팔을 당하는 유저의 email
    */
    void unFollow(String reqEmail, String deletedUserEmail);

    /*
        targetUserEmail의 유저의 팔로우 or 팔로워 목록을 조회합니다.

        reqEmail        : 요청자의 email
        targetUserEmail : 친구목록을 조회할 유저의 email
        pageNum         : 친구목록 페이지 번호
        searchKeyword   : 검색어. 검색어가 없다면 null을 보내주세요.
        searchType      : 팔로워와 팔로잉중 어느것을 조회할 것인지 보내주세요.
    */
    FriendFindPagingResDto findFriendPageByUser(String reqEmail, String targetUserEmail, Long pageNum,
                                                FriendSearchType searchType, String searchKeyword);


    /*
        targetUser의 팔로워, 팔로잉수를 조회합니다.

        targetUser : 팔로워, 팔로잉수를 조회할 유저
    */
    FriendCntResDto findFriendCnt(User targetUser);

    /*
        reqUser가 targetUser를 팔로우했는 지 여부 반환

        reqUser    : 요청자
        targetUser : 팔로우여부를 체크할 유저
    */
    boolean isFollow(User reqUser, User targetUser);

}
