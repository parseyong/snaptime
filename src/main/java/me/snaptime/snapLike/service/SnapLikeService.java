package me.snaptime.snapLike.service;

public interface SnapLikeService {

    /*
        스냅 좋아요 토글.
        스냅에 좋아요를 눌렀으면 취소, 안눌렀다면 좋아요를 추가합니다.
        좋아요 토글여부에 따라 다른 메시지를 반환합니다.

        reqEmail : 요청자의 email
        snapId   : 좋아요를 누를 스냅의 id
    */
    String toggleSnapLike(String reqEmail, Long snapId);

    /*
        스냅에 달린 좋아요개수를 조회합니다.

        snapId : 스냅 id
    */
    Long findSnapLikeCnt(Long snapId);

    /*
        요청자가 해당 스냅에 좋아요를 눌렀는지 여부를 반환합니다.

        snapId   : 좋아요를 눌렀는지 여부를 체크할 snap의 id
        reqEmail : 요청자의 email
    */
    boolean isLikedSnap(Long snapId, String reqEmail);

}
