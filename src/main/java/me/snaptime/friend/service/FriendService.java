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

        senderLoginId   : 팔로우요청을 보내는 유저의 loginId
        receiverLoginId : 팔로우요청을 받는 유저의 loginId
    */
    void sendFollow(String senderLoginId, String receiverLoginId);

    /*
        팔로우요청을 수락하거나 거절합니다.
        수락(sender(팔로우 요청자)의 팔로잉 +1, receiver(팔로우 수락자)의 팔로워 +1)
        거절(sender(팔로우 요청자)의 팔로워 -1, receiver(팔로우 거절자)의 팔로잉 -1)

        sender   : 팔로우 요청을 보냈던 유저
        receiver : 팔로우 요청을 받았던 유저
        isAccept : 팔로우 수락여부
    */
    String acceptFollow(User sender, User receiver, boolean isAccept);

    /*
        언팔로우를 합니다.
        (reqLoginId(삭제자)의 팔로잉 -1, deletedUser의 팔로워 -1)

        reqLoginId         : 요청자(언팔을 하려는 유저)의 loginId
        deletedUserLoginId : 언팔을 당하는 유저의 loginId
    */
    void unFollow(String reqLoginId, String deletedUserLoginId);

    /*
        targetLoginId의 유저의 팔로우 or 팔로워 목록을 조회합니다.

        reqLoginId      : 요청자의 loginId
        targetLoginId   : 친구목록을 조회할 유저의 loginId
        pageNum         : 친구목록 페이지 번호
        searchKeyword   : 검색어. 검색어가 없다면 null을 보내주세요.
        searchType      : 팔로워와 팔로잉중 어느것을 조회할 것인지 보내주세요.
    */
    FriendFindPagingResDto findFriendPageByUser(String reqLoginId, String targetLoginId, Long pageNum,
                                                FriendSearchType searchType, String searchKeyword);


    /*
        targetUser의 팔로워, 팔로잉수를 조회합니다.

        targetUser : 팔로워, 팔로잉수를 조회할 유저
    */
    FriendCntResDto findFriendCnt(User targetUser);

    /*
        reqUser가 targetUser를 팔로우했는 지 여부 반환

        reqUser : 요청자
        targetUser : 팔로우여부를 체크할 유저
    */
    boolean isFollow(User reqUser, User targetUser);

}
