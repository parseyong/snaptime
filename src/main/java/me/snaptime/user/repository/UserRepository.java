package me.snaptime.user.repository;

import me.snaptime.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Long>, UserQdslRepository {
    Optional<User> findByEmail(String email);
}
