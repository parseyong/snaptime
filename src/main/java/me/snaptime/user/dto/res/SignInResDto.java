package me.snaptime.user.dto.res;


import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SignInResDto(
        String accessToken,
        String refreshToken
) {
    public static SignInResDto toDto(String accessToken, String refreshToken){
        return SignInResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
