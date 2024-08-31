package me.snaptime.reply.service;

import me.snaptime.reply.dto.req.ChildReplyAddReqDto;
import me.snaptime.reply.dto.req.ParentReplyAddReqDto;
import me.snaptime.reply.dto.req.ReplyUpdateReqDto;
import me.snaptime.reply.dto.res.ChildReplyPagingResDto;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;

public interface ReplyService {

    /*
        댓글을 추가합니다.
        snap작성자에게 알림이 추가됩니다.

        reqLoginId : 요청자의 loginId
        snapId     : 댓글을 추가할 snap의 Id
        parentReplyAddReqDto : 댓글내용이 담긴 dto
    */
    void addParentReply(String reqLoginId, Long snapId, ParentReplyAddReqDto parentReplyAddReqDto);

    /*
        댓글에 대댓글을 추가합니다.

        reqLoginId          : 요청자의 loginId
        parentReplyId       : 부모댓글의 id
        childReplyAddReqDto : 댓글내용과 태그유저의 id가 담긴 dto
    */
    void addChildReply(String reqLoginId, Long parentReplyId, ChildReplyAddReqDto childReplyAddReqDto);

    /*
        댓글을 최신순으로 20개씩 조회합니다.
        커뮤니티기능이므로 페이징처리를 합니다.

        snapId  : 댓글을 조회할 스냅의 id
        pageNum : 페이지번호
    */
    ParentReplyPagingResDto findParentReplyPage(Long snapId, Long pageNum);

    /*
        대댓글을 최신순으로 20개씩 조회합니다.
        커뮤니티기능이므로 페이징처리를 합니다.

        parentReplyId  : 대댓글을 조회할 댓글의 id
        pageNum        : 페이지번호
    */
    ChildReplyPagingResDto findChildReplyPage(Long parentReplyId, Long pageNum);

    /*
        댓글을 수정합니다.

        reqLoginId        : 요청자의 loginId
        parentReplyId     : 변경할 부모댓글의 id
        replyUpdateReqDto : 변경할 댓글내용이 담긴 dto
    */
    void updateParentReply(String reqLoginId ,Long parentReplyId, ReplyUpdateReqDto replyUpdateReqDto);

    /*
        댓글을 수정합니다.

        reqLoginId    : 요청자의 loginId
        childReplyId  : 변경할 대댓글의 id
        replyUpdateReqDto : 변경할 대댓글내용이 담긴 dto
    */
    void updateChildReply(String reqLoginId, Long childReplyId, ReplyUpdateReqDto replyUpdateReqDto);

    /*
        댓글을 삭제합니다.
        댓글에 달린 모든 대댓글까지 함께 삭제됩니다.

        reqLoginId    : 요청자의 loginId
        parentReplyId : 삭제할 댓글의 id
    */
    void deleteParentReply(String reqLoginId, Long parentReplyId);

    /*
        대댓글을 삭제합니다.

        reqLoginId   : 요청자의 loginId
        childReplyId : 삭제할 대댓글의 id
    */
    void deleteChildReply(String reqLoginId, Long childReplyId);
}
