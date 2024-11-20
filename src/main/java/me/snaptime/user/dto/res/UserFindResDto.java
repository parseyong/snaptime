package me.snaptime.user.dto.res;

import com.querydsl.core.Tuple;
import lombok.AccessLevel;
import lombok.Builder;

import static me.snaptime.user.domain.QUser.user;

@Builder(access = AccessLevel.PRIVATE)
public record UserFindResDto(

        Long userId,
        String email,
        String profilePhotoURL,
        String nickName
){
    public static UserFindResDto toDto(Tuple tuple, String profilePhotoURL){

        return UserFindResDto.builder()
                .userId(tuple.get(user.userId))
                .email(tuple.get(user.email))
                .profilePhotoURL(profilePhotoURL)
                .nickName(tuple.get(user.nickname))
                .build();
    }
}
