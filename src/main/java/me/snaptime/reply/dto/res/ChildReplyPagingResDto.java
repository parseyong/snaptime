package me.snaptime.reply.dto.res;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record ChildReplyPagingResDto(

        List<ChildReplyFindResDto> childReplyFindResDtos,
        boolean hasNextPage

) {
    public static ChildReplyPagingResDto toDto(List<ChildReplyFindResDto> childReplyFindResDtos, boolean hasNextPage){

        return ChildReplyPagingResDto.builder()
                .childReplyFindResDtos(childReplyFindResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
