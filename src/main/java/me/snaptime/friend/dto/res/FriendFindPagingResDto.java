package me.snaptime.friend.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FriendFindPagingResDto(

        List<FriendInfoResDto> friendInfoResDtos,
        boolean hasNextPage
) {
    public static FriendFindPagingResDto toDto(List<FriendInfoResDto> friendInfoResDtos, boolean hasNextPage){

        return FriendFindPagingResDto.builder()
                .friendInfoResDtos(friendInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
