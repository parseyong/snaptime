package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.alarm.dto.res.AlarmFindResDto;
import me.snaptime.alarm.enums.AlarmType;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.service.FriendService;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;
import me.snaptime.reply.service.ReplyService;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;
import me.snaptime.snap.service.SnapService;
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
    private final FriendService friendService;
    private final SnapService snapService;
    private final ReplyService replyService;
    private final UrlComponent urlComponent;


    @Override
    @Transactional
    public SnapFindDetailResDto readSnapAlarm(String reqLoginId, Long snapAlarmId) {

        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId, snapAlarm.getReceiver().getLoginId());

        snapAlarm.readAlarm();
        snapAlarmRepository.save(snapAlarm);

        return snapService.findSnap(snapAlarm.getSnap().getSnapId(), reqLoginId);
    }

    @Override
    @Transactional
    public String readFollowAlarm(String reqLoginId, Long followAlarmId, boolean isAccept) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId, followAlarm.getReceiver().getLoginId());

        String message = friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), isAccept);
        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);

        return message;
    }

    @Override
    @Transactional
    public ParentReplyPagingResDto readReplyAlarm(String reqLoginId, Long replyAlarmId) {
        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId, replyAlarm.getReceiver().getLoginId());

        replyAlarm.readAlarm();
        replyAlarmRepository.save(replyAlarm);

        return replyService.findParentReplyPage(replyAlarm.getSnap().getSnapId(), 1L);
    }

    @Override
    public AlarmFindAllResDto findAllAlarms(String reqLoginId) {
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return AlarmFindAllResDto.toDto(findSortedAlarms(reqUser,false), findSortedAlarms(reqUser,true));
    }

    @Override
    public Long findNotReadAlarmCnt(String reqLoginId) {
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return followAlarmRepository.countByReceiverAndIsRead(reqUser,false)+
                snapAlarmRepository.countByReceiverAndIsRead(reqUser, false)+
                replyAlarmRepository.countByReceiverAndIsRead(reqUser, false);
    }

    @Override
    @Transactional
    public void deleteAlarm(String reqLoginId, Long alarmId, AlarmType alarmType) {

        // 팔로우 알림일 경우 거절처리 후 삭제
        if(alarmType == AlarmType.FOLLOW){
            FollowAlarm followAlarm = followAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, followAlarm.getReceiver().getLoginId());
            friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), false);
            followAlarmRepository.delete(followAlarm);
        }

        // 댓글알림일 경우 바로삭제
        else if(alarmType == AlarmType.REPLY){
            ReplyAlarm replyAlarm = replyAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, replyAlarm.getReceiver().getLoginId());
            replyAlarmRepository.delete(replyAlarm);
        }

        // 스냅(스냅태그, 좋아요)에 대한 알림일 경우 바로 삭제
        else{
            SnapAlarm snapAlarm = snapAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, snapAlarm.getReceiver().getLoginId());
            snapAlarmRepository.delete(snapAlarm);
        }
    }

    // 자신한테 온 알림인지 여부체크
    private void isMyAlarm(String reqLoginId, String alarmReceiverLoginId){

        if(!reqLoginId.equals(alarmReceiverLoginId))
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
            String snapPhotoURL = urlComponent.makePhotoURL(snapAlarm.getSnap().getFileName(),false);
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
            String snapPhotoURL = urlComponent.makePhotoURL(replyAlarm.getSnap().getFileName(),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(replyAlarm.getCreatedDate());

            AlarmFindResDto alarmFindResDto = AlarmFindResDto.toReplyAlarmDto(senderProfilePhotoURL, snapPhotoURL, timeAgo, replyAlarm);
            alarmFindResDtos.add(alarmFindResDto);
        });
    }
}
