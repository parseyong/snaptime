package me.snaptime.photo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.photo.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/photo")
    @Operation(summary = "photo 조회", description = "사진을 조회합니다.")
    @Parameters({
            @Parameter(name = "fileName", description = "찾을 사진(snap 사진 or 프로필 사진)의 fileName을 입력해주세요." +
                                                        "이미지타입(ex. png)를 포함한 파일이름을 입력해주세요.<br> " +
                                                        "암호화된 사진인 경우 인증을 한 뒤 복호화하여 반환합니다.<br>" +
                                                        "기본적으로는 인증토큰없이 사진조회가 가능하지만 암호화된 사진의 경우에는 인증토큰이 필요합니다."),
            @Parameter(name = "isEncrypted", description = "암호화여부를 입력해주세요.")
    })
    public ResponseEntity<byte[]> findPhoto(
            final @AuthenticationPrincipal String reqLoginId,
            final @RequestParam("fileName") String fileName,
            final @RequestParam("isEncrypted") boolean isEncrypted) {

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(photoService.findPhoto(reqLoginId, fileName, isEncrypted));
    }
}
