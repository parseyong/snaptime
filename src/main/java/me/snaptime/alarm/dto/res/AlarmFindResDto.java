package me.snaptime.alarm.dto.res;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.enums.AlarmType;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
public record AlarmFindResDto(

        Long alarmId,
        String snapPhotoURL,
        String senderName,
        String senderProfilePhotoURL,
        String timeAgo,
        String previewText,
        AlarmType alarmType,
        Long snapId,
        String senderEmail,
        @Getter
        LocalDateTime createdDate

) {
    public static AlarmFindResDto toFollowAlarmDto(String senderProfilePhotoURL, String timeAgo, FollowAlarm followAlarm){

        return AlarmFindResDto.builder()
                .alarmId(followAlarm.getFollowAlarmId())
                .snapPhotoURL(null)
                .senderName(followAlarm.getSender().getNickname())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(followAlarm.getAlarmType())
                .createdDate(followAlarm.getCreatedDate())
                .snapId(null)
                .senderEmail(followAlarm.getSender().getEmail())
                .build();
    }

    public static AlarmFindResDto toSnapAlarmDto(String senderProfilePhotoURL, String snapPhotoURL,
                                                 String timeAgo, SnapAlarm snapAlarm){

        return AlarmFindResDto.builder()
                .alarmId(snapAlarm.getSnapAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(snapAlarm.getSender().getNickname())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(snapAlarm.getAlarmType())
                .createdDate(snapAlarm.getCreatedDate())
                .snapId(snapAlarm.getSnap().getSnapId())
                .senderEmail(snapAlarm.getSender().getEmail())
                .build();
    }

    public static AlarmFindResDto toReplyAlarmDto(String senderProfilePhotoURL, String snapPhotoURL,
                                                  String timeAgo, ReplyAlarm replyAlarm){

        return AlarmFindResDto.builder()
                .alarmId(replyAlarm.getReplyAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(replyAlarm.getSender().getNickname())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(replyAlarm.getReplyMessage())
                .alarmType(replyAlarm.getAlarmType())
                .createdDate(replyAlarm.getCreatedDate())
                .snapId(replyAlarm.getSnap().getSnapId())
                .senderEmail(replyAlarm.getSender().getEmail())
                .build();
    }
}
