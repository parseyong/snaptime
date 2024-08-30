package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ParentReply;
import org.springframework.data.repository.CrudRepository;

public interface ParentReplyRepository extends CrudRepository<ParentReply,Long>, ParentReplyQdslRepository {

}
