package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ChildReply;
import org.springframework.data.repository.CrudRepository;

public interface ChildReplyRepository extends CrudRepository<ChildReply,Long>, ChildReplyQdslRepository {

}
