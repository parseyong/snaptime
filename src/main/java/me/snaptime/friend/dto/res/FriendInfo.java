package me.snaptime.friend.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.user.domain.QUser.user;

@Builder
public record FriendInfo(

        String foundLoginId,
        String profilePhotoURL,
        String foundUserName,
        Long friendId,
        boolean isMyFriend
) {
    public static FriendInfo toDto(Tuple tuple, String profilePhotoURL, boolean isMyFriend){
        return FriendInfo.builder()
                .foundLoginId(tuple.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .foundUserName(tuple.get(user.name))
                .friendId(tuple.get(friend.friendId))
                .isMyFriend(isMyFriend)
                .build();
    }
}
