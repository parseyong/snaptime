package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
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
