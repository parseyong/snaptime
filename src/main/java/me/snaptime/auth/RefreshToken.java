package me.snaptime.auth;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
// refreshToken의 유효시간이 지나면 자동삭제
@RedisHash(value = "refreshToken", timeToLive = 2592000)
public class RefreshToken {

    @Id
    @Indexed
    private String loginId;

    private String refreshToken;

    public RefreshToken(String loginId, String refreshToken){

        this.loginId = loginId;
        this.refreshToken = refreshToken;
    }
}
