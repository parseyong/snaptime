package me.snaptime.crawling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/crawling")
@RequiredArgsConstructor
@Tag(name = "[Crawling] Crawling API")
public class CrawlingController {
    private final CrawlingService crawlingService;

    @GetMapping("/harufilm")
    @Operation(summary = "하루필름 크롤링 API", description = "하루필름 크롤링 API입니다.")
    @Parameter(name = "crawlingURL", description = "QR Code의 URL")
    public ResponseEntity<CommonResponseDto<byte[]>> harufilmCrawling(
            final @RequestParam("crawlingURL") @NotBlank(message = "크롤링할 하루필름 사진URL값을 입력해주세요.") String crawlingURL) {

        byte[] photoBytes = crawlingService.findPhotoByCrawling(ProviderBrand.HARU, crawlingURL);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(CommonResponseDto.of("하루필름 크롤링 성공",photoBytes));
    }

    @GetMapping("/onepercent")
    @Operation(summary = "1Percent 크롤링 API", description = "1Percent 크롤링 API입니다.")
    @Parameter(name = "crawlingURL", description = "QR Code의 URL")
    ResponseEntity<CommonResponseDto<byte[]>> onePercentCrawling(
            final @RequestParam("crawlingURL") @NotBlank(message = "크롤링할 onepercent 사진URL값을 입력해주세요.") String crawlingURL) {

        byte[] photoBytes = crawlingService.findPhotoByCrawling(ProviderBrand.ONE_PERCENT, crawlingURL);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(CommonResponseDto.of("onepercent 크롤링 성공",photoBytes));
    }

    @Operation(summary = "Studio808 크롤링 API", description = "Studio808 크롤링 API입니다.")
    @Parameter(name = "crawlingURL", description = "QR Code의 URL")
    @GetMapping("/studio808")
    ResponseEntity<CommonResponseDto<byte[]>> studio808Crawling(
            final @RequestParam("crawlingURL") @NotBlank(message = "크롤링할 Studio8080 사진URL값을 입력해주세요.") String crawlingURL) {

        byte[] photoBytes = crawlingService.findPhotoByCrawling(ProviderBrand.STUDIO_808, crawlingURL);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(CommonResponseDto.of("Studio8080 크롤링 성공",photoBytes));
    }

    @Operation(summary = "PhotoSignature 크롤링 API", description = "PhotoSignature 크롤링 API입니다.")
    @Parameter(name = "crawlingURL", description = "QR Code의 URL")
    @GetMapping("/photosignature")
    ResponseEntity<CommonResponseDto<byte[]>> photoSignatureCrawling(
            final @RequestParam("crawlingURL") @NotBlank(message = "크롤링할 PhotoSignature 사진URL값을 입력해주세요.") String crawlingURL) {

        byte[] photoBytes = crawlingService.findPhotoByCrawling(ProviderBrand.PHOTO_SIGNATURE, crawlingURL);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG)
                .body(CommonResponseDto.of("PhotoSignature 크롤링 성공",photoBytes));
    }


}
