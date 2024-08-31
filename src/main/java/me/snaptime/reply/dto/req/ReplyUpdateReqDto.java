package me.snaptime.reply.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReplyUpdateReqDto(
        @Schema(
                example = "변경할 댓글 or 대댓글 내용",
                description = "댓글 or 대댓글로 등록할 내용을 입력해주세요"
        )
        @NotBlank(message = "댓글내용을 입력해주세요.")
        String replyMessage
) {
}
