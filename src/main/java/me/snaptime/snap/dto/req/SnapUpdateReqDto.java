package me.snaptime.snap.dto.req;

import org.springframework.web.multipart.MultipartFile;

public record SnapUpdateReqDto(
        String oneLineJournal,
        MultipartFile multipartFile
) {
}
