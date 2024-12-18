package me.snaptime.reply.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.component.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.dto.req.ChildReplyAddReqDto;
import me.snaptime.reply.dto.req.ParentReplyAddReqDto;
import me.snaptime.reply.dto.req.ReplyUpdateReqDto;
import me.snaptime.reply.dto.res.ChildReplyFindResDto;
import me.snaptime.reply.dto.res.ChildReplyPagingResDto;
import me.snaptime.reply.dto.res.ParentReplyFindResDto;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.reply.service.ReplyService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import me.snaptime.util.TimeAgoCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {

    private final ParentReplyRepository parentReplyRepository;
    private final ChildReplyRepository childReplyRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final UrlComponent urlComponent;
    private final AlarmAddService alarmAddService;

    @Override
    @Transactional
    public void addParentReply(String reqEmail, Long snapId, ParentReplyAddReqDto parentReplyAddReqDto){
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .writer(reqUser)
                        .snap(snap)
                        .content(parentReplyAddReqDto.replyMessage())
                        .build()
        );

        alarmAddService.addReplyAlarm(reqUser, snap.getWriter(), snap, parentReplyAddReqDto.replyMessage());
    }

    @Transactional
    public void addChildReply(String reqEmail, Long parentReplyId, ChildReplyAddReqDto childReplyAddReqDto){
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));
        Snap snap = parentReply.getSnap();

        // 태그유저가 없는 댓글 등록이면
        if( childReplyAddReqDto.tagUserEmail().isBlank()){
            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .writer(reqUser)
                            .content(childReplyAddReqDto.replyMessage())
                            .build()
            );
        }
        // 태그유저가 있는 댓글등록이면
        else{
            User tagUser = userRepository.findByEmail(childReplyAddReqDto.tagUserEmail())
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .writer(reqUser)
                            .tagUser(tagUser)
                            .content(childReplyAddReqDto.replyMessage())
                            .build()
            );
            alarmAddService.addReplyAlarm(reqUser, tagUser, snap, childReplyAddReqDto.replyMessage());
        }

        alarmAddService.addReplyAlarm(reqUser, snap.getWriter(), snap, childReplyAddReqDto.replyMessage());
    }

    public ParentReplyPagingResDto findParentReplyPage(Long snapId, Long pageNum){

        List<Tuple> tuples = parentReplyRepository.findReplyPage(snapId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<ParentReplyFindResDto> parentReplyFindResDtos = tuples.stream().map(tuple ->
        {
            String profilePhotoURL = urlComponent.makePhotoURL(tuple.get(user.profilePhotoName),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(tuple.get(parentReply.lastModifiedDate));
            Long childReplyCnt = childReplyRepository.countByParentReplyId(tuple.get(parentReply.parentReplyId));

            return ParentReplyFindResDto.toDto(tuple,profilePhotoURL,timeAgo,childReplyCnt);
        }).collect(Collectors.toList());

        return ParentReplyPagingResDto.toDto(parentReplyFindResDtos, hasNextPage);
    }

    public ChildReplyPagingResDto findChildReplyPage(Long parentReplyId, Long pageNum){

        List<ChildReply> childReplies = childReplyRepository.findReplyPage(parentReplyId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPageByChildReplies(childReplies,20L);

        List<ChildReplyFindResDto> childReplyFindResDtos = childReplies.stream().map(childReply ->
        {
            String profilePhotoURL = urlComponent.makePhotoURL( childReply.getWriter().getProfilePhotoName(),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo( childReply.getLastModifiedDate());

            return ChildReplyFindResDto.toDto(childReply, profilePhotoURL, timeAgo);
        }).collect(Collectors.toList());

        return ChildReplyPagingResDto.toDto(childReplyFindResDtos, hasNextPage);
    }

    @Transactional
    public void updateParentReply(String reqEmail ,Long parentReplyId, ReplyUpdateReqDto replyUpdateReqDto){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqEmail, parentReply.getWriter().getEmail());
        parentReply.updateReply(replyUpdateReqDto.replyMessage());
        parentReplyRepository.save(parentReply);
    }

    @Transactional
    public void updateChildReply(String reqEmail, Long childReplyId, ReplyUpdateReqDto replyUpdateReqDto){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqEmail,childReply.getWriter().getEmail());
        childReply.updateReply(replyUpdateReqDto.replyMessage());
        childReplyRepository.save(childReply);
    }

    @Transactional
    public void deleteParentReply(String reqEmail, Long parentReplyId){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqEmail,parentReply.getWriter().getEmail());
        parentReplyRepository.delete(parentReply);
    }

    @Transactional
    public void deleteChildReply(String reqEmail, Long childReplyId){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqEmail,childReply.getWriter().getEmail());
        childReplyRepository.delete(childReply);
    }

    private void checkMyReply(String reqEmail, String targetUserEmail){

        if(!targetUserEmail.equals(reqEmail))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);
    }
}