package me.snaptime.alarm.service;

import me.snaptime.alarm.enums.AlarmType;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;

public interface AlarmAddService {

    /*
        스냅알림(태그,좋아요)을 생성합니다. 스냅에 대한 알림타입이 2개이므로 알림타입을 인자로 받습니다.

        sender               : 행위(좋아요,스냅에 태그)를 하여 알림을 보내는 유저
        receiver             : 알림을 받는 유저
        snapFindResDtos      : 행위가 이루어진 스냅
        alarmType            : 알림타입
    */
    void addSnapAlarm(User sender, User receiver, Snap snap, AlarmType alarmType);

    /*
       팔로우알림을 생성합니다.

       sender   : 행위(팔로우 요청)를 하여 알림을 보내는 유저
       receiver : 알림을 받는 유저(팔로우요청을 받은 유저)
   */
    void addFollowAlarm(User sender, User receiver);

    /*
       댓글알림을 생성합니다.

       sender       : 행위(댓글달기)를 하여 알림을 보내는 유저
       receiver     : 알림을 받는 유저
       snapFindResDtos         : 행위가 이루어진 스냅
       replyMessage : 댓글 미리보기(댓글이 수정되더라도 댓글미리보기는 변경되지 않습니다.)
   */
    void addReplyAlarm(User sender, User receiver, Snap snap, String replyMessage);
}
