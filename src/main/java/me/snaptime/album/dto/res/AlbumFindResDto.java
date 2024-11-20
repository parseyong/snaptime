package me.snaptime.album.dto.res;

import lombok.AccessLevel;
import lombok.Builder;
import me.snaptime.album.domain.Album;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record AlbumFindResDto(
        Long albumId,
        String albumName,
        List<String> thumbnailPhotoURLs
) {
    public static AlbumFindResDto toDto(Album album, List<String> thumbnailPhotoURLs){

        return AlbumFindResDto.builder()
                .albumId(album.getAlbumId())
                .albumName(album.getAlbumName())
                .thumbnailPhotoURLs(thumbnailPhotoURLs)
                .build();
    }
}
