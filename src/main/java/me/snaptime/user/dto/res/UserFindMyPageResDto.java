package me.snaptime.user.dto.res;


import lombok.AccessLevel;
import lombok.Builder;
import me.snaptime.user.domain.User;

@Builder(access = AccessLevel.PRIVATE)
public record UserFindMyPageResDto(
    Long userId,
    String nickname,
    String email,
    String birthDay,
    String profilePhotoURL

){
    public static UserFindMyPageResDto toDto(User user, String profilePhotoURL){
        return UserFindMyPageResDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .profilePhotoURL(profilePhotoURL)
                .build();
    }

}
