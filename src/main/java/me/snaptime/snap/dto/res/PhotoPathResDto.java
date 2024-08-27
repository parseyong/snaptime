package me.snaptime.snap.dto.res;

import lombok.Builder;

@Builder
public record PhotoPathResDto(
        String filePath,
        String fileName
) {

}
