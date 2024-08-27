package me.snaptime.snap.repository;

import me.snaptime.album.domain.Album;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapRepository extends JpaRepository<Snap, Long>, SnapQdslRepository {

    List<Snap> findAllByAlbum(Album album);
    Long countByUser(User user);
}
