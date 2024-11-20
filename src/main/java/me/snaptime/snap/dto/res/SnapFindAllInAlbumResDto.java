package me.snaptime.snap.dto.res;

import lombok.AccessLevel;
import lombok.Builder;
import me.snaptime.album.domain.Album;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record SnapFindAllInAlbumResDto(

        Long albumId,
        String albumName,
        List<SnapFindResDto> snapFindResDtos

) {
    public static SnapFindAllInAlbumResDto toDto(List<SnapFindResDto> snapFindResDtos, Album album){
        return SnapFindAllInAlbumResDto.builder()
                .snapFindResDtos(snapFindResDtos)
                .albumId(album.getAlbumId())
                .albumName(album.getAlbumName())
                .build();
    }
}
