package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReplyAlarmRepository extends CrudRepository<ReplyAlarm,Long> {

    List<ReplyAlarm> findAllByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
