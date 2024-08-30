package me.snaptime.reply.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface ParentReplyQdslRepository {

    /*
        댓글을 페이징조회합니다.

        snapId  : 댓글을 조회할 snap의 Id
        pageNum : 페이지번호
    */
    List<Tuple> findReplyPage(Long snapId, Long pageNum);
}
