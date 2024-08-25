package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.snap.dto.req.CreateSnapReqDto;
import me.snaptime.snap.dto.req.ModifySnapReqDto;
import me.snaptime.snap.dto.res.SnapFindAllInAlbumResDto;
import me.snaptime.snap.dto.res.SnapFindDetailResDto;
import me.snaptime.snap.service.SnapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
@Slf4j
public class SnapController {

    private final SnapService snapService;

    @Operation(summary = "Snap 생성", description = "Empty Value를 보내지마세요")
    @PostMapping(value = "/snap", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Long>> createSnap(
            final @ModelAttribute CreateSnapReqDto createSnapReqDto,
            final @AuthenticationPrincipal String reqLoginId) {

        Long snapId = snapService.createSnap(createSnapReqDto,reqLoginId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto<>(
                "스냅이 정상적으로 저장되었습니다.",
                        snapId
        ));
    }

    @GetMapping("/albums/{albumId}/snaps")
    @Operation(summary = "Album에 포함된 snap조회", description = "Album에 포함된 snap을 모두 조회합니다.")
    public ResponseEntity<CommonResponseDto<SnapFindAllInAlbumResDto>> findAllSnapInAlbum(
            final @PathVariable("albumId") Long albumId,
            final @AuthenticationPrincipal String reqLoginId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범 내 snap조회 성공", snapService.findAllSnapInAlbum(reqLoginId, albumId)));
    }

    @Operation(summary = "Snap 찾기", description = "Snap 한 개 가져오기")
    @Parameter(name = "id", description = "찾을 Snap의 albumId")
    @GetMapping(value = "/snaps/{id}")
    public ResponseEntity<CommonResponseDto<SnapFindDetailResDto>> findSnap(
            final @PathVariable("id") Long id,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 불러와졌습니다.",
                        snapService.findSnap(id, uId)
                )
        );
    }

    @Operation(summary = "Snap 수정", description = "Snap 수정하기")
    @Parameters({
            @Parameter(name = "isPrivate", description = "변경 할 상태"),
            @Parameter(name = "snapId", description = "변경 할 Snap의 ID"),
            @Parameter(name = "tagUserLoginIds", description = "변경 할 TagID")
    })
    @PutMapping(value = "/snap", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Long>> modifySnap(
            final @ModelAttribute ModifySnapReqDto modifySnapReqDto,
            final @RequestParam boolean isPrivate,
            final @RequestParam Long snapId,
            final @RequestParam(required = false) List<String> tagUserLoginIds,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 수정되었습니다.",
                        snapService.modifySnap(snapId, modifySnapReqDto, uId, tagUserLoginIds, isPrivate)
                )
        );
    }

    @Operation(summary = "Snap 공개상태 변경", description = "Snap 공개 상태를 변경합니다.")
    @PostMapping("/snaps/visibility")
    @Parameters({
            @Parameter(name = "snapId", description = "변경할 Snap albumId"),
            @Parameter(name = "isPrivate", description = "변경할 상태")
    })
    public ResponseEntity<CommonResponseDto<Void>> changeVisibility(
            final @RequestParam("snapId") Long snapId,
            final @RequestParam("isPrivate") boolean isPrivate,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.changeVisibility(snapId, uId, isPrivate);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "게시글의 상태가 성공적으로 변경되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Snap 앨범 위치 변경", description = "Snap의 앨범 위치를 변경합니다.")
    @PostMapping("/snaps/album")
    @Parameters({
            @Parameter(name = "snapId", description = "위치를 변경할 Snap Id"),
            @Parameter(name = "albumId", description = "이동할 Album Id")
    })
    ResponseEntity<CommonResponseDto<Void>> relocateSnap(
            final @RequestParam Long snapId,
            final @RequestParam Long albumId,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.relocateSnap(snapId, albumId, uId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅의 위치가 " + albumId + "번 앨범으로 변경되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Snap 삭제", description = "스냅을 삭제합니다.")
    @DeleteMapping(value = "/snap")
    @Parameter(name = "snapId", description = "삭제할 Snap ID")
    ResponseEntity<CommonResponseDto<Void>> deleteSnap(
            final @RequestParam Long snapId,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.deleteSnap(snapId, uId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        snapId + "번 스냅이 삭제되었습니다.",
                        null
                )
        );
    }
}
