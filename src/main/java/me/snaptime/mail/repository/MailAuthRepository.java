package me.snaptime.mail.repository;

import me.snaptime.mail.domain.MailAuthInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MailAuthRepository extends CrudRepository<MailAuthInfo, String> {

    Optional<MailAuthInfo> findEmailInfoByEmail(String email);
}
