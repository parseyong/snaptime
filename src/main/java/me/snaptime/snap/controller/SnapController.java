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

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
public class SnapController {

    private final SnapService snapService;

    @PostMapping(value = "/snaps", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Snap 생성", description = "스냅을 생성합니다.")
    public ResponseEntity<CommonResponseDto<Long>> createSnap(
            final @AuthenticationPrincipal String reqLoginId,
            @ModelAttribute @Valid SnapAddReqDto snapAddReqDto) {

        snapService.addSnap(reqLoginId, snapAddReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("스냅이 정상적으로 저장되었습니다.", null));
    }

    @GetMapping(value = "/snaps/{snapId}")
    @Operation(summary = "1개의 Snap 상세조회", description = "1개 Snap의 상세정보(태그정보,좋아요 수 등등..)를 조회합니다.")
    @Parameter(name = "snapId", description = "조회할 snap의 id")
    public ResponseEntity<CommonResponseDto<SnapFindDetailResDto>> findSnapDetail(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("snapId") Long snapId) {

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponseDto.of("스냅이 정상적으로 불러와졌습니다.", snapService.findSnapDetail(reqLoginId, snapId)));
    }

    @PatchMapping(value = "/snaps/{snapId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Snap 수정", description = "Snap의 한줄일기, 사진, 태그정보를 수정합니다. <br>" +
                                                    "공개여부, 앨범위치를 변경하려면 다른 요청을 보내주세요.")
    public ResponseEntity<CommonResponseDto<Void>> updateSnap(
            final @AuthenticationPrincipal String reqLoginId,
            @ModelAttribute @Valid SnapUpdateReqDto snapUpdateReqDto,
            final @PathVariable("snapId") Long snapId) {

        snapService.updateSnap(reqLoginId, snapId, snapUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅이 정상적으로 수정되었습니다.", null));
    }

    @PatchMapping("/snaps/{snapId}/visibility")
    @Operation(summary = "Snap 공개상태 변경", description = "Snap 공개 상태를 변경합니다.")
    @Parameters({
            @Parameter(name = "snapId", description = "공개상태를 변경할 snapId를 입력해주세요."),
            @Parameter(name = "isPrivate", description = "변경할 상태를 입력해주세요.")
    })
    public ResponseEntity<CommonResponseDto<Void>> updateVisibility(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("snapId") Long snapId,
            final @RequestParam("isPrivate") boolean isPrivate) {

        snapService.updateSnapVisibility(reqLoginId, snapId, isPrivate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("게시글의 상태가 성공적으로 변경되었습니다.", null));
    }

    @PatchMapping("/albums/{albumId}/snaps/{snapId}/move")
    @Operation(summary = "Snap 앨범 위치 변경", description = "Snap의 앨범 위치를 변경합니다.")
    @Parameters({
            @Parameter(name = "snapId", description = "위치를 변경할 Snap Id"),
            @Parameter(name = "albumId", description = "이동할 Album Id")
    })
    ResponseEntity<CommonResponseDto<Void>> relocateSnap(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("snapId") Long snapId,
            final @PathVariable("albumId") Long albumId) {

        snapService.updateSnapPosition(reqLoginId,snapId,albumId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅위치 변경이 완료되었습니다.",null));
    }

    @DeleteMapping(value = "/snaps/{snapId}")
    @Operation(summary = "Snap 삭제", description = "스냅을 삭제합니다.")
    @Parameter(name = "snapId", description = "삭제할 Snap ID")
    ResponseEntity<CommonResponseDto<Void>> deleteSnap(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("snapId") Long snapId) {

        snapService.deleteSnap(reqLoginId, snapId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅이 삭제되었습니다.", null));
    }

    @GetMapping("/albums/{albumId}/snaps/all")
    @Operation(summary = "자신의 Album에 포함된 snap조회", description = "자신의 Album에 포함된 snap을 공개여부와 상관없이 조회합니다.<br>" +
                                                                "이 요청은 자신의 앨범에 저장된 스냅을 보는 요청으로 " +
                                                                "다른사람의 앨범을 보려면 profile api를 이용해주세요.")
    public ResponseEntity<CommonResponseDto<SnapFindAllInAlbumResDto>> findAllSnapInAlbum(
            final @PathVariable("albumId") Long albumId,
            final @AuthenticationPrincipal String reqLoginId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범 내 snap조회 성공", snapService.findAllSnapInAlbum(reqLoginId, albumId)));
    }

}
