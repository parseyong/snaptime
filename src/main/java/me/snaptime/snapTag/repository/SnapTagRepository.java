package me.snaptime.snapTag.repository;

import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.domain.SnapTag;
import me.snaptime.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SnapTagRepository extends CrudRepository<SnapTag,Long> {

    boolean existsBySnapAndTagUser(Snap snap, User tagUser);

    List<SnapTag> findBySnap(Snap snap);
}
