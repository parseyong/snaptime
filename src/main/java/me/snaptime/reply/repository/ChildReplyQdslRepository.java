package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ChildReply;

import java.util.List;

public interface ChildReplyQdslRepository {

    /*
        대댓글을 페이징조회합니다.

        parentReplyId  : 댓글을 조회할 snap의 Id
        pageNum        : 페이지번호
    */
    List<ChildReply> findReplyPage(Long parentReplyId, Long pageNum);

    /*
        부모댓글에 달린 대댓글의 개수를 반환합니다.

        parentReplyId  : 대댓글 개수를 조회할 부모댓글의 id
    */
    Long countByParentReplyId(Long parentReplyId);
}
