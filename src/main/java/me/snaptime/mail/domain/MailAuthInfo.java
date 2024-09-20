package me.snaptime.mail.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "authMessage", timeToLive = 180)
public class MailAuthInfo {

    @Id
    @Indexed
    private String email;

    private String authMessage;
    private boolean isVerify;

    public MailAuthInfo(String email, String authMessage, boolean isVerify){

        this.email = email;
        this.authMessage = authMessage;
        this.isVerify=isVerify;
    }
}
