package me.snaptime.mail.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.mail.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/mail")
@RequiredArgsConstructor
@Tag(name = "[Mail] Mail API")
public class MailController {

    private final MailService mailService;

    @PostMapping("/{reqEmail}")
    @Operation(summary = "해당 메일로 인증메시지를 전송합니다.", description = "3분 이내에 인증번호 확인을 해야합니다.")
    @Parameter(name = "reqEmail" , description = "메일을 보낼 이메일주소를 입력해주세요.", required = true)
    public ResponseEntity<CommonResponseDto<Void>> sendAuthMessage(final @PathVariable String reqEmail) {

        mailService.sendAuthMessage(reqEmail);
        return ResponseEntity.status(HttpStatus.OK).body( CommonResponseDto.of("인증메시지 전송완료", null) );
    }

    @GetMapping("/{reqEmail}")
    @Operation(summary = "인증번호 검증", description = "보낸 인증번호를 통해 이메일인증을 진행합니다.")
    @Parameters({
            @Parameter(name = "reqEmail" , description = "인증할 이메일 주소를 입력해주세요.", required = true),
            @Parameter(name = "authMessage", description = "인증번호를 입력해주세요", required = true)
    })
    public ResponseEntity<CommonResponseDto<Void>> verifyAuthMessage(
            final @PathVariable String reqEmail,
            final @RequestParam @NotNull(message = "인증번호를 입력해주세요.") String authMessage) {

        mailService.verifyAuthMessage(reqEmail,authMessage);
        return ResponseEntity.status(HttpStatus.OK).body( CommonResponseDto.of("메일인증이 완료되었습니다.", null) );
    }
}
