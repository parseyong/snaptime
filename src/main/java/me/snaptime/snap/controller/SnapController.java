package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.snap.dto.req.SnapAddReqDto;
import me.snaptime.snap.dto.req.SnapUpdateReqDto;
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;
import me.snaptime.snap.service.SnapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
public class SnapController {

    private final SnapService snapService;

    @PostMapping(value = "/albums/snaps", consumes = {MediaType.APPLICATION_JSON_VALUE,
                                                        MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Snap 생성", description = "스냅을 생성합니다.<br>" +
            "현재 @RequestPart로 파일데이터를 받는 기능이 스웨거에서 지원이 되지 않습니다. 포스트맨으로 요청테스트를 하시길 바랍니다.")
    @Parameter(name = "multipartFile", description = "크롤링API를 통해 얻은 사진을 MultipartFile형식으로 보내주세요.")
    public ResponseEntity<CommonResponseDto<Long>> createSnap(
            final @AuthenticationPrincipal String reqEmail,
            @RequestPart @Valid SnapAddReqDto snapAddReqDto,
            final @RequestPart MultipartFile multipartFile) {

        snapService.addSnap(reqEmail, snapAddReqDto, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("스냅이 정상적으로 저장되었습니다.", null));
    }

    @GetMapping(value = "/albums/snaps/{snapId}")
    @Operation(summary = "1개의 Snap 상세조회", description = "1개 Snap의 상세정보(태그정보,좋아요 수 등등..)를 조회합니다." +
                                                            "비공개 스냅의 경우 자신의 쓴 snap이여야만 조회가 가능합니다.")
    @Parameter(name = "snapId", description = "조회할 snap의 id")
    public ResponseEntity<CommonResponseDto<SnapFindDetailResDto>> findSnapDetail(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("snapId") Long snapId) {

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponseDto.of("스냅이 정상적으로 불러와졌습니다.", snapService.findSnapDetail(reqEmail, snapId)));
    }

    @PatchMapping(value = "/albums/snaps/{snapId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
                                                                    MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Snap 수정", description = "Snap의 한줄일기, 사진, 태그정보를 수정합니다. <br>" +
                                                    "공개여부, 앨범위치를 변경하려면 다른 요청을 보내주세요.<br>" +
            "현재 @RequestPart로 파일데이터를 받는 기능이 스웨거에서 지원이 되지 않습니다. 포스트맨으로 요청테스트를 하시길 바랍니다.")
    @Parameters({
            @Parameter(name = "snapId", description = "변경할 스냅의 id"),
            @Parameter(name = "multipartFile", description = "크롤링API를 통해 얻은 사진을 MultipartFile형식으로 보내주세요.<br>" +
                                                            "사진변경을 원하지 않는다면 파일데이터를 보내지 마세요.")
    })
    public ResponseEntity<CommonResponseDto<Void>> updateSnap(
            final @AuthenticationPrincipal String reqEmail,
            @RequestPart @Valid SnapUpdateReqDto snapUpdateReqDto,
            final @RequestPart(required = false) MultipartFile multipartFile,
            final @PathVariable("snapId") Long snapId) {

        snapService.updateSnap(reqEmail, snapId, snapUpdateReqDto, multipartFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅이 정상적으로 수정되었습니다.", null));
    }

    @PatchMapping("/albums/snaps/{snapId}/visibility/{isPrivate}")
    @Operation(summary = "Snap 공개상태 변경", description = "Snap 공개 상태를 변경합니다.")
    @Parameters({
            @Parameter(name = "snapId", description = "공개상태를 변경할 snapId를 입력해주세요."),
            @Parameter(name = "isPrivate", description = "변경할 상태를 입력해주세요.")
    })
    public ResponseEntity<CommonResponseDto<Void>> updateVisibility(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("snapId") Long snapId,
            final @PathVariable("isPrivate") boolean isPrivate) {

        snapService.updateSnapVisibility(reqEmail, snapId, isPrivate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("게시글의 상태가 성공적으로 변경되었습니다.", null));
    }

    @PatchMapping("/albums/{albumId}/snaps/{snapId}/move")
    @Operation(summary = "Snap 앨범 위치 변경", description = "Snap의 앨범 위치를 변경합니다.")
    @Parameters({
            @Parameter(name = "snapId", description = "위치를 변경할 Snap Id"),
            @Parameter(name = "albumId", description = "이동할 Album Id")
    })
    ResponseEntity<CommonResponseDto<Void>> updateSnapPosition(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("snapId") Long snapId,
            final @PathVariable("albumId") Long albumId) {

        snapService.updateSnapPosition(reqEmail,snapId,albumId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅위치 변경이 완료되었습니다.",null));
    }

    @DeleteMapping(value = "/albums/snaps/{snapId}")
    @Operation(summary = "Snap 삭제", description = "스냅을 삭제합니다.")
    @Parameter(name = "snapId", description = "삭제할 Snap ID")
    ResponseEntity<CommonResponseDto<Void>> deleteSnap(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("snapId") Long snapId) {

        snapService.deleteSnap(reqEmail, snapId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅이 삭제되었습니다.", null));
    }

    @GetMapping("/albums/{albumId}/snaps/all")
    @Operation(summary = "Album에 포함된 snap조회", description = "Album에 포함된 snap을 조회합니다.<br>" +
                                                                "다른사람의 앨범안의 snap을 조회할 경우 공개스냅만 조회됩니다.<br>" +
                                                                "나의 앨범을 조회할 경우 비공개스냅도 같이 조회됩니다.")
    @Parameter(name = "albumId", description = "모든 스냅을 조회할 앨범의 id")
    public ResponseEntity<CommonResponseDto<SnapFindAllInAlbumResDto>> findAllSnapInAlbum(
            final @PathVariable("albumId") Long albumId,
            final @AuthenticationPrincipal String reqEmail) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범 내 snap조회 성공", snapService.findAllSnapInAlbum(reqEmail, albumId)));
    }

}
