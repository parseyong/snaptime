package me.snaptime.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.alarm.enums.AlarmType;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowAlarm extends BaseTimeEntity {

    @Id
    @Column(name = "follow_alarm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followAlarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="sender_id",nullable = false)
    // 행위(팔로우 요청)을 통해 receiver에게 알림을 보내는 유저
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="receiver_id",nullable = false)
    // 알림을 받는 유저
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "alarm_type")
    private AlarmType alarmType;

    @Column(name = "is_read",nullable = false)
    private boolean isRead;

    @Builder
    protected FollowAlarm(User sender, User receiver, AlarmType alarmType){
        this.sender=sender;
        this.receiver=receiver;
        this.alarmType=alarmType;
        this.isRead = false;
    }

    public void readAlarm(){
        isRead = true;
    }
}
