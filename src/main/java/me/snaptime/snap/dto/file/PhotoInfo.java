package me.snaptime.snap.dto.file;

import lombok.Builder;

@Builder
public record PhotoInfo(
        String filePath,
        String fileName
) {

}
