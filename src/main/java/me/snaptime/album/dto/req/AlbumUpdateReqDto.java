package me.snaptime.album.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AlbumUpdateReqDto(

        @Schema(
                example = "앨범 이름",
                description = "변경할 앨범의 이름을 보내주세요."
        )
        @NotBlank(message = "앨범이름을 보내주세요")
        String newAlbumName
) {
}
