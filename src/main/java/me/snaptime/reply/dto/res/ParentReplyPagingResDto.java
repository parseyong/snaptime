package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record ParentReplyPagingResDto(

        List<ParentReplyFindResDto> parentReplyFindResDtos,
        boolean hasNextPage
) {
    public static ParentReplyPagingResDto toDto(List<ParentReplyFindResDto> parentReplyFindResDtos, boolean hasNextPage){
        return ParentReplyPagingResDto.builder()
                .parentReplyFindResDtos(parentReplyFindResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
