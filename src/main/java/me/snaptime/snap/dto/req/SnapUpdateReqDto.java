package me.snaptime.snap.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SnapUpdateReqDto(
        @Schema(
                example = "스냅 한줄일기",
                description = "스냅에 등록할 새로운 한줄일기를 입력해주세요. " +
                        "변경을 원하지 않다면 기존 한줄일기값을 입력해주세요."
        )
        @NotBlank(message = "한줄일기를 입력해주세요.")
        String oneLineJournal,

        @Schema(
                description = "스냅에 태그할 유저의 email을 입력해주세요. " +
                        "변경을 원하지 않다면 반드시 기존 태그유저정보를 입력해주세요."
        )
        List<String> tagUserEmails
) {
}
