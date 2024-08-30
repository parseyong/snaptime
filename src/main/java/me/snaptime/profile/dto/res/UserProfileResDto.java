package me.snaptime.profile.dto.res;

import lombok.Builder;
import me.snaptime.friend.dto.res.FriendCntResDto;

@Builder
public record UserProfileResDto(
        Long userId,
        String loginId,
        String nickName,
        String profilePhotoURL,
        FriendCntResDto friendCntResDto,
        Long snapCnt,
        Boolean isFollow
){
}
