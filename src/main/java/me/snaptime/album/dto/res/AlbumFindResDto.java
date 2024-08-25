package me.snaptime.album.dto.res;

import lombok.Builder;
import me.snaptime.album.domain.Album;

@Builder
public record AlbumFindResDto(
        Long albumId,
        String albumName,
        String thumbnailPhotoURL
) {
    public static AlbumFindResDto toDto(Album album,String thumbnailPhotoURL){

        if(thumbnailPhotoURL == null){
            return AlbumFindResDto.builder()
                    .albumId(album.getAlbumId())
                    .albumName(album.getAlbumName())
                    .build();
        }
        else{
            return AlbumFindResDto.builder()
                    .albumId(album.getAlbumId())
                    .albumName(album.getAlbumName())
                    .thumbnailPhotoURL(thumbnailPhotoURL)
                    .build();
        }
    }
}
