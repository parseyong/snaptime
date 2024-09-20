package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
public record SignInReqDto(

        @Schema(
                example = "tester1@naver.com",
                description = "유저의 email를 입력해주세요"
        )
        @NotBlank(message = "유저 email입력은 필수입니다.")
        String email,

        @Schema(
                example = "1234",
                description = "유저의 password를 입력해주세요"
        )
        @NotBlank(message = "유저 password입력은 필수입니다.")
        String password

) {

}
