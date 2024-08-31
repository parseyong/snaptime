package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
public record SignInReqDto(

        @Schema(
                example = "kang4746",
                description = "유저의 loginId를 입력해주세요"
        )
        @NotBlank(message = "유저 loginId 입력은 필수입니다.")
        String loginId,

        @Schema(
                example = "password",
                description = "유저의 password를 입력해주세요"
        )
        @NotBlank(message = "유저 password 입력은 필수입니다.")
        String password

) {

}
