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
}
