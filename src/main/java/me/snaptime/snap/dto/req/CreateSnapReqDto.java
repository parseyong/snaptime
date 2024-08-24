package me.snaptime.snap.dto.req;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateSnapReqDto(
        String oneLineJournal,
        MultipartFile multipartFile,
        boolean isPrivate,
        Long album_id,
        List<String> tagUserLoginIds
) {
}
