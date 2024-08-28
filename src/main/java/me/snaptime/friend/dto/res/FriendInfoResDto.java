package me.snaptime.friend.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.user.domain.QUser.user;

@Builder
public record FriendInfoResDto(

        String friendLoginId,
        String profilePhotoURL,
        String friendUserNickName,
        Long friendId,
        boolean isMyFriend
) {
    public static FriendInfoResDto toDto(Tuple tuple, String profilePhotoURL, boolean isMyFriend){
        return FriendInfoResDto.builder()
                .friendLoginId(tuple.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .friendUserNickName(tuple.get(user.nickname))
                .friendId(tuple.get(friend.friendId))
                .isMyFriend(isMyFriend)
                .build();
    }
}
