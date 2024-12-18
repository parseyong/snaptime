package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.alarm.dto.res.AlarmFindResDto;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.TimeAgoCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService {

    private final SnapAlarmRepository snapAlarmRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final ReplyAlarmRepository replyAlarmRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;

    @Override
    @Transactional
    public void readSnapAlarm(String reqEmail, Long snapAlarmId) {

        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        checkMyAlarm(reqEmail, snapAlarm.getReceiver().getEmail());

        snapAlarm.readAlarm();
        snapAlarmRepository.save(snapAlarm);
    }

    @Override
    @Transactional
    public void readFollowAlarm(String reqEmail, Long followAlarmId) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        checkMyAlarm(reqEmail, followAlarm.getReceiver().getEmail());

        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);
    }

    @Override
    @Transactional
    public void readReplyAlarm(String reqEmail, Long replyAlarmId) {
        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        checkMyAlarm(reqEmail, replyAlarm.getReceiver().getEmail());

        replyAlarm.readAlarm();
        replyAlarmRepository.save(replyAlarm);
    }

    @Override
    public AlarmFindAllResDto findAllAlarms(String reqEmail) {
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return AlarmFindAllResDto.toDto(findSortedAlarms(reqUser,false), findSortedAlarms(reqUser,true));
    }

    @Override
    public Long findNotReadAlarmCnt(String reqEmail) {
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return followAlarmRepository.countByReceiverAndIsRead(reqUser,false)+
                snapAlarmRepository.countByReceiverAndIsRead(reqUser, false)+
                replyAlarmRepository.countByReceiverAndIsRead(reqUser, false);
    }

    @Override
    @Transactional
    public void deleteFollowAlarm(String reqEmail, Long followAlarmId){

        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        checkMyAlarm(reqEmail, followAlarm.getReceiver().getEmail());
        followAlarmRepository.delete(followAlarm);
    }

    @Override
    @Transactional
    public void deleteSnapAlarm(String reqEmail, Long snapAlarmId){

        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        checkMyAlarm(reqEmail, snapAlarm.getReceiver().getEmail());
        snapAlarmRepository.delete(snapAlarm);
    }

    @Override
    @Transactional
    public void deleteReplyAlarm(String reqEmail, Long replyAlarmId){

        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        checkMyAlarm(reqEmail, replyAlarm.getReceiver().getEmail());
        replyAlarmRepository.delete(replyAlarm);
    }

    // 자신한테 온 알림인지 여부체크
    private void checkMyAlarm(String reqEmail, String alarmReceiverEmail){

        if(!reqEmail.equals(alarmReceiverEmail))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALARM);

    }

    // 알림을 최신순으로 정렬하여 조회합니다.
    private List<AlarmFindResDto> findSortedAlarms(User reqUser, boolean isRead){

        List<AlarmFindResDto> alarmFindResDtos = new ArrayList<>();
        findFollowAlarms(reqUser, isRead, alarmFindResDtos);
        findReplyAlarms(reqUser, isRead, alarmFindResDtos);
        findSnapAlarms(reqUser, isRead, alarmFindResDtos);

        // 최신순으로 정렬
        alarmFindResDtos.sort(Comparator.comparing(AlarmFindResDto::getCreatedDate).reversed());
        return alarmFindResDtos;
    }

    private void findFollowAlarms(User reqUser, boolean isRead, List<AlarmFindResDto> alarmFindResDtos){

        List<FollowAlarm> followAlarms = followAlarmRepository.findAllByReceiverAndIsRead(reqUser,isRead);

        followAlarms.forEach(followAlarm -> {

            User sender = followAlarm.getSender();
            String senderProfilePhotoURL = urlComponent.makePhotoURL(sender.getProfilePhotoName(),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(followAlarm.getCreatedDate());

            AlarmFindResDto alarmFindResDto = AlarmFindResDto.toFollowAlarmDto(senderProfilePhotoURL, timeAgo, followAlarm);
            alarmFindResDtos.add(alarmFindResDto);
        });
    }

    private void findSnapAlarms(User reqUser, boolean isRead, List<AlarmFindResDto> alarmFindResDtos){

        List<SnapAlarm> snapAlarms = snapAlarmRepository.findAllByReceiverAndIsRead(reqUser,isRead);

        snapAlarms.forEach(snapAlarm -> {

            User sender = snapAlarm.getSender();
            String senderProfilePhotoURL = urlComponent.makePhotoURL(sender.getProfilePhotoName(),false);
            String snapPhotoURL = urlComponent.makePhotoURL(snapAlarm.getSnap().getFileName(),snapAlarm.getSnap().isPrivate());
            String timeAgo = TimeAgoCalculator.findTimeAgo(snapAlarm.getCreatedDate());

            AlarmFindResDto alarmFindResDto = AlarmFindResDto.toSnapAlarmDto(senderProfilePhotoURL, snapPhotoURL, timeAgo, snapAlarm);
            alarmFindResDtos.add(alarmFindResDto);
        });
    }

    private void findReplyAlarms(User reqUser, boolean isRead, List<AlarmFindResDto> alarmFindResDtos){

        List<ReplyAlarm> replyAlarms = replyAlarmRepository.findAllByReceiverAndIsRead(reqUser,isRead);

        replyAlarms.forEach(replyAlarm -> {

            User sender = replyAlarm.getSender();
            String senderProfilePhotoURL = urlComponent.makePhotoURL(sender.getProfilePhotoName(),false);
            String snapPhotoURL = urlComponent.makePhotoURL(replyAlarm.getSnap().getFileName(),replyAlarm.getSnap().isPrivate());
            String timeAgo = TimeAgoCalculator.findTimeAgo(replyAlarm.getCreatedDate());

            AlarmFindResDto alarmFindResDto = AlarmFindResDto.toReplyAlarmDto(senderProfilePhotoURL, snapPhotoURL, timeAgo, replyAlarm);
            alarmFindResDtos.add(alarmFindResDto);
        });
    }

}
