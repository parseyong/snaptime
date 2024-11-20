package me.snaptime.friend.dto.res;

import com.querydsl.core.Tuple;
import lombok.AccessLevel;
import lombok.Builder;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.user.domain.QUser.user;

@Builder(access = AccessLevel.PRIVATE)
public record FriendInfoResDto(

        String friendEmail,
        String profilePhotoURL,
        String friendUserNickName,
        Long friendId,
        boolean isFollow
) {
    public static FriendInfoResDto toDto(Tuple tuple, String profilePhotoURL, boolean isFollow){
        return FriendInfoResDto.builder()
                .friendEmail(tuple.get(user.email))
                .profilePhotoURL(profilePhotoURL)
                .friendUserNickName(tuple.get(user.nickname))
                .friendId(tuple.get(friend.friendId))
                .isFollow(isFollow)
                .build();
    }
}
