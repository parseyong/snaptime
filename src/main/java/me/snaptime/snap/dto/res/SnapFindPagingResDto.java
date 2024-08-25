package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record SnapFindPagingResDto(

    List<SnapFindDetailResDto> snapFindDetailResDtos,
    boolean hasNextPage
) {
    public static SnapFindPagingResDto toDto(List<SnapFindDetailResDto> snapFindDetailResDtos, boolean hasNextPage){

        return SnapFindPagingResDto.builder()
                .snapFindDetailResDtos(snapFindDetailResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
