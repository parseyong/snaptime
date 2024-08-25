package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record SnapFindAllInAlbumResDto(

        Long albumId,
        String albumName,
        List<SnapFindResDto> snapFindResDtos

) {
}
