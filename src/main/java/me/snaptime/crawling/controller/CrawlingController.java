package me.snaptime.crawling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.crawling.enums.ProviderBrand;
import me.snaptime.crawling.service.CrawlingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/crawling")
@RequiredArgsConstructor
@Tag(name = "[Crawling] Crawling API")
public class CrawlingController {
    private final CrawlingService crawlingService;

    @GetMapping("/{providerBrand}")
    @Operation(summary = "크롤링 API", description = "크롤링 API입니다.")
    @Parameters({
        @Parameter(name = "crawlingURL", description = "QR Code의 URL"),
        @Parameter(name = "providerBrand", description = "크롤링할 브랜드 명")
    })
    public ResponseEntity<CommonResponseDto<byte[]>> crawling(
            final @RequestParam("crawlingURL") @NotBlank(message = "크롤링할 URL값을 입력해주세요.") String crawlingURL,
            final @PathVariable("providerBrand") ProviderBrand providerBrand) {

        byte[] photoBytes = crawlingService.findPhotoByCrawling(providerBrand, crawlingURL);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(CommonResponseDto.of(providerBrand.name()+"크롤링 성공",photoBytes));
    }
}
