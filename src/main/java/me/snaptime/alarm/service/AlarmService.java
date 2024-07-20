package me.snaptime.alarm.service;

import me.snaptime.alarm.common.AlarmType;
import me.snaptime.reply.dto.res.FindParentReplyResDto;
import me.snaptime.snap.dto.res.FindSnapResDto;

public interface AlarmService {

    /*
        snapAlarm을 읽음처리합니다.
        읽음 처리 후 해당 스냅을 조회합니다.
    */
    FindSnapResDto readSnapAlarm(String loginId, Long snapAlarmId);

    //팔로우요청을 수락 or거절한 뒤 FollowAlarm을 읽음처리합니다.
    String readFollowAlarm(String loginId, Long followAlarmId, boolean isAccept);

    /*
        ReplyAlarm을 읽음처리합니다.
        읽음처리 후 해당 댓글의 1페이지로 이동합니다.
    */
    FindParentReplyResDto readReplyAlarm(String loginId, Long replyAlarmId);

    //유저의 모든 알림을 불러옵니다.
    Object getAlarmList(Long loginId);

    // 읽지않은 알림이 몇개인지 조회합니다.
    Long findNotReadAlarmCnt(Long loginId);

    /*
        알림을 삭제합니다.
        읽지않은 팔로우요청에 대한 알림의 경우 자동으로 요청거절이 됩니다.
    */
    void deleteAlarm(String loginId, Long alarmId, AlarmType alarmType);
}