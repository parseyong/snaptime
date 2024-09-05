package me.snaptime.snap.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SnapAddReqDto(

        @Schema(
                example = "스냅 한줄일기",
                description = "스냅에 등록할 한줄일기를 입력해주세요."
        )
        @NotBlank(message = "한줄일기를 입력해주세요.")
        String oneLineJournal,

        @Schema(
                example = "false",
                description = "스냅의 공개여부를 보내주세요."
        )
        @NotNull(message = "공개여부를 입력해주세요.")
        boolean isPrivate,

        @Schema(
                example = "1",
                description = "스냅을 저장할 앨범ID를 입력해주세요. " +
                            "입력하지 않으면 기본앨범에 저장됩니다."
        )
        Long albumId,

        @Schema(
                description = "스냅에 태그할 유저의 loginId를 입력해주세요. " +
                                "없으면 입력하지 않아도 됩니다."
        )
        List<String> tagUserLoginIds
) {
}
