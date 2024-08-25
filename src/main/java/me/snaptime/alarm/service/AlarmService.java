package me.snaptime.alarm.service;

import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;


public interface AlarmService {

    /*
        snapAlarm을 읽음처리한 후 읽음처리 후 해당 스냅을 조회합니다.
        reqLoginId  : 요청자의 loginId
        snapAlarmId : 조회하려는 스냅알림 id
    */
    SnapFindDetailResDto readSnapAlarm(String reqLoginId, Long snapAlarmId);

    /*
        팔로우요청을 수락 or거절한 뒤 FollowAlarm을 읽음처리합니다.
        reqLoginId    : 요청자의 loginId
        followAlarmId : 조회하려는 팔로우알림 id
        isAccept      : 팔로우 수락 여부
    */
    String readFollowAlarm(String reqLoginId, Long followAlarmId, boolean isAccept);

    /*
        ReplyAlarm을 읽음처리합니다. 읽음처리 후 해당 댓글의 1페이지로 이동합니다.
        reqLoginId   : 요청자의 loginId
        replyAlarmId : 조회하려는 댓글알림 id
    */
    ParentReplyPagingResDto readReplyAlarm(String reqLoginId, Long replyAlarmId);

    /*
        유저의 모든 알림을 불러옵니다.
        reqLoginId : 요청자의 loginId
    */
    AlarmFindAllResDto findAllAlarms(String reqLoginId);

    /*
        읽지않은 알림이 몇개인지 조회합니다.
        reqLoginId : 요청자의 loginId
    */
    Long findNotReadAlarmCnt(String reqLoginId);

    /*
        알림을 삭제합니다.
        읽지않은 팔로우요청에 대한 알림의 경우 자동으로 요청거절이 됩니다.
        reqLoginId : 요청자의 loginId
        alarmId    : 삭제하려는 알림의 id
        alarmType  : 알림타입, 알림타입에따라 삭제로직이 달라지므로 알림타입을 인자로 받습니다.
    */
    void deleteAlarm(String reqLoginId, Long alarmId, AlarmType alarmType);
}
