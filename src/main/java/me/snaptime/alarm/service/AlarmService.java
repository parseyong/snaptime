package me.snaptime.alarm.service;

import me.snaptime.alarm.dto.req.AlarmDeleteReqDto;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;


public interface AlarmService {

    /*
        snapAlarm을 읽음처리합니다.

        reqLoginId  : 요청자의 loginId
        snapAlarmId : 조회하려는 스냅알림 albumId
    */
    void readSnapAlarm(String reqLoginId, Long snapAlarmId);

    /*
        FollowAlarm을 읽음처리합니다.

        reqLoginId    : 요청자의 loginId
        followAlarmId : 조회하려는 팔로우알림 albumId
    */
    void readFollowAlarm(String reqLoginId, Long followAlarmId);

    /*
        ReplyAlarm을 읽음처리합니다.

        reqLoginId   : 요청자의 loginId
        replyAlarmId : 조회하려는 댓글알림 albumId
    */
    void readReplyAlarm(String reqLoginId, Long replyAlarmId);

    /*
        유저의 모든 알림을 불러옵니다.
        읽은알림과 읽지않은 알림들에 대한 정보가 담긴 DTO를 반환합니다.

        reqLoginId : 요청자의 loginId
    */
    AlarmFindAllResDto findAllAlarms(String reqLoginId);

    /*
        읽지않은 알림이 몇개인지 조회합니다.
        읽지않은 알림개수를 반환합니다.

        reqLoginId : 요청자의 loginId
    */
    Long findNotReadAlarmCnt(String reqLoginId);

    /*
        알림을 삭제합니다. 읽지않은 알림도 삭제가 가능합니다.

        reqLoginId        : 요청자의 loginId
        alarmId           : 삭제하려는 알림의 albumId
        alarmDeleteReqDto : 알림타입이 담긴 dto
    */
    void deleteAlarm(String reqLoginId, Long alarmId, AlarmDeleteReqDto alarmDeleteReqDto);

}
