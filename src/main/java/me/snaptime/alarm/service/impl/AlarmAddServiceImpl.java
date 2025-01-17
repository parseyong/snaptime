package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.enums.AlarmType;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmAddServiceImpl implements AlarmAddService {

    private final SnapAlarmRepository snapAlarmRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final ReplyAlarmRepository replyAlarmRepository;

    @Override
    public void addSnapAlarm(User sender, User receiver, Snap snap, AlarmType alarmType) {
        if(sender.getUserId() == receiver.getUserId())
            return ;

        SnapAlarm snapAlarm = SnapAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .snap(snap)
                .alarmType(alarmType)
                .build();

        snapAlarmRepository.save(snapAlarm);
    }

    @Override
    public void addFollowAlarm(User sender, User receiver) {
        FollowAlarm followAlarm = FollowAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .alarmType(AlarmType.FOLLOW)
                .build();

        followAlarmRepository.save(followAlarm);
    }

    @Override
    public void addReplyAlarm(User sender, User receiver, Snap snap, String replyMessage) {
        if(sender.getUserId() == receiver.getUserId())
            return ;

        ReplyAlarm replyAlarm = ReplyAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .snap(snap)
                .replyMessage(replyMessage)
                .alarmType(AlarmType.REPLY)
                .build();

        replyAlarmRepository.save(replyAlarm);
    }
}
