package me.snaptime.snap.repository;

import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnapRepository extends JpaRepository<Snap, Long>, SnapPagingRepository{
    Long countByUser(User user);

    Optional<Snap> findByFileName(String fileName);
}
