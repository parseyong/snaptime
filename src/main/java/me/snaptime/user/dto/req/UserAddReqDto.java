package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserAddReqDto(
        @Schema(
                example = "tester1",
                description = "유저의 이름을 입력해주세요"
        )
        @NotBlank(message = "유저 이름 입력은 필수입니다.")
        String nickName,

        @Schema(
                example = "tester1@naver.com",
                description = "유저의 이메일을 입력해주세요"
        )
        @NotBlank(message = "유저 이메일 입력은 필수입니다.")
        String email,

        @Schema(
                example = "1234",
                description = "유저의 비밀번호를 입력해주세요"
        )
        @NotBlank(message = "유저 비밀번호 입력은 필수입니다.")
        String password,

        @Schema(
                example = "1999-10-29",
                description = "유저의 생년월일을 입력해주세요"
        )
        @NotBlank(message = "유저 생년월일 입력은 필수입니다.")
        String birthDay
){}
