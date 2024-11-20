package me.snaptime.snap.dto.res;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
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
