package me.snaptime.snapLike.service;

public interface SnapLikeService {

    /*
        스냅 좋아요 토글. 자신의 스냅에는 좋아요를 누를 수 없습니다.
        스냅에 좋아요를 눌렀으면 취소, 안눌렀다면 좋아요를 추가합니다.
        좋아요 토글여부에 따라 다른 메시지를 반환합니다.

        reqLoginId : 요청자의 loginId
        snapId     : 좋아요를 누를 스냅의 id
    */
    String toggleSnapLike(String reqLoginId, Long snapId);

    /*
        스냅에 달린 좋아요개수를 조회합니다.

        snapId : 스냅 id
    */
    Long findSnapLikeCnt(Long snapId);

    /*
        요청자가 해당 스냅에 좋아요를 눌렀는지 여부를 반환합니다.

        snapId     : 좋아요를 눌렀는지 여부를 체크할 snap의 id
        reqLoginId : 요청자의 loginId
    */
    boolean isLikedSnap(Long snapId, String reqLoginId);

}
