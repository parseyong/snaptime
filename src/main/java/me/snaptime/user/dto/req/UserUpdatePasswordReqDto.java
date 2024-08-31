package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordReqDto(

        @Schema(
                example = "1234",
                description = "새로운 비밀번호를 입력해주세요."
        )
        @NotBlank(message = "패스워드 입력은 필수입니다.")
        String newPassword
) {
}
