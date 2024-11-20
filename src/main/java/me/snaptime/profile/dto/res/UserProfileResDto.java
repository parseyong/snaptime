package me.snaptime.profile.dto.res;

import lombok.AccessLevel;
import lombok.Builder;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.user.domain.User;

@Builder(access = AccessLevel.PRIVATE)
public record UserProfileResDto(
        Long userId,
        String email,
        String nickName,
        String profilePhotoURL,
        FriendCntResDto friendCntResDto,
        Long snapCnt,
        Boolean isFollow
){
    public static UserProfileResDto toDto(User targetUser, FriendCntResDto friendCntResDto,
                                          boolean isFollow, Long snapCnt, String profilePhotoURL){
        return UserProfileResDto.builder()
                .userId(targetUser.getUserId())
                .email(targetUser.getEmail())
                .nickName(targetUser.getNickname())
                .profilePhotoURL(profilePhotoURL)
                .friendCntResDto(friendCntResDto)
                .snapCnt(snapCnt)
                .isFollow(isFollow)
                .build();
    }
}
