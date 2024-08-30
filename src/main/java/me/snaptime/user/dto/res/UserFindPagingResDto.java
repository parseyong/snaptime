package me.snaptime.user.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record UserFindPagingResDto(

        List<UserFindResDto> userFindResDtos,
        boolean hasNextPage
){
    public static UserFindPagingResDto toDto(List<UserFindResDto> userFindResDtos, boolean hasNextPage){
        return UserFindPagingResDto.builder()
                .userFindResDtos(userFindResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
