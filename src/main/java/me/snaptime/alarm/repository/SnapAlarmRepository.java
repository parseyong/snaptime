package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SnapAlarmRepository extends CrudRepository<SnapAlarm,Long> {

    List<SnapAlarm> findAllByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
