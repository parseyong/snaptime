package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FollowAlarmRepository extends CrudRepository<FollowAlarm,Long> {

    List<FollowAlarm> findAllByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
