package me.snaptime.reply.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChildReplyAddReqDto(
        @Schema(
                example = "대댓글내용입니다.",
                description = "대댓글로 등록할 내용을 입력해주세요"
        )
        @NotBlank(message = "댓글내용을 입력해주세요.")
        String replyMessage,

        @Schema(
                example = "홍길동",
                description = "태그할 유저의 loginId를 입력해주세요. 없으면 입력하지 않아도 됩니다."
        )
        String tagLoginId
) {
}
