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
    public void addParentReply(String reqLoginId, Long snapId, String content){
        User reqUser = findUserByLoginId(reqLoginId);
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        parentReplyRepository.save(
                ParentReply.builder()
                        .writer(reqUser)
                        .snap(snap)
                        .content(content)
                        .build()
        );

        alarmAddService.addReplyAlarm(reqUser, snap.getWriter(), snap, content);
    }

    @Transactional
    public void addChildReply(String reqLoginId, Long parentReplyId, ChildReplyAddReqDto childReplyAddReqDto){
        User reqUser = findUserByLoginId(reqLoginId);

        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        // 태그유저가 없는 댓글 등록이면
        if( childReplyAddReqDto.tagLoginId() == null){
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
            User tagUser = userRepository.findByLoginId(childReplyAddReqDto.tagLoginId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            childReplyRepository.save(
                    ChildReply.builder()
                            .parentReply(parentReply)
                            .writer(reqUser)
                            .tagUser(tagUser)
                            .content(childReplyAddReqDto.replyMessage())
                            .build()
            );
        }
    }

    public ParentReplyPagingResDto findParentReplyPage(Long snapId, Long pageNum){

        List<Tuple> tuples = parentReplyRepository.findReplyPage(snapId,pageNum);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<ParentReplyFindResDto> parentReplyFindResDtos = tuples.stream().map(tuple ->
        {
            String profilePhotoURL = urlComponent.makePhotoURL(tuple.get(user.profilePhotoName),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(tuple.get(parentReply.lastModifiedDate));
            return ParentReplyFindResDto.toDto(tuple,profilePhotoURL,timeAgo);
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
    public void updateParentReply(String reqLoginId ,Long parentReplyId, String newContent){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqLoginId, parentReply.getWriter().getLoginId());
        parentReply.updateReply(newContent);
        parentReplyRepository.save(parentReply);
    }

    @Transactional
    public void updateChildReply(String reqLoginId, Long childReplyId, String newContent){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqLoginId,childReply.getWriter().getLoginId());
        childReply.updateReply(newContent);
        childReplyRepository.save(childReply);
    }

    @Transactional
    public void deleteParentReply(String reqLoginId, Long parentReplyId){
        ParentReply parentReply = parentReplyRepository.findById(parentReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqLoginId,parentReply.getWriter().getLoginId());
        parentReplyRepository.delete(parentReply);
    }

    @Transactional
    public void deleteChildReply(String reqLoginId, Long childReplyId){
        ChildReply childReply = childReplyRepository.findById(childReplyId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REPLY_NOT_EXIST));

        checkMyReply(reqLoginId,childReply.getWriter().getLoginId());
        childReplyRepository.delete(childReply);
    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

    private void checkMyReply(String reqLoginId, String targetLoginId){

        if(!targetLoginId.equals(reqLoginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_REPLY);
    }
}