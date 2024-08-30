package me.snaptime.user.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.user.domain.QUser.user;

@Builder
public record UserFindResDto(

        Long userId,
        String loginId,
        String profilePhotoURL,
        String nickName
){
    public static UserFindResDto toDto(Tuple tuple, String profilePhotoURL){

        return UserFindResDto.builder()
                .userId(tuple.get(user.userId))
                .loginId(tuple.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .nickName(tuple.get(user.nickname))
                .build();
    }
}
