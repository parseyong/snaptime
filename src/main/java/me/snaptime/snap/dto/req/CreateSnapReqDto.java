package me.snaptime.snap.dto.req;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateSnapReqDto(
        String oneLineJournal,

        @NotNull
        MultipartFile multipartFile,
        boolean isPrivate,
        Long album_id,
        List<String> tagUserLoginIds
) {
}
