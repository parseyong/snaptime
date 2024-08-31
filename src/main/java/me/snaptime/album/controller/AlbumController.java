package me.snaptime.album.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.dto.req.AlbumAddReqDto;
import me.snaptime.album.dto.req.AlbumUpdateReqDto;
import me.snaptime.album.dto.res.AlbumFindResDto;
import me.snaptime.album.service.AlbumService;
import me.snaptime.common.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Album] Album API")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping(path = "/albums/with-thumnail")
    @Operation(summary = "Album 목록(썸네일 포함) 조회", description = "사용자의 Album 목록(썸네일 포함)을 조회합니다.<br>" +
                                                                    "썸네일로 선택되는 앨범은 한개입니다.<br>" +
                                                                    "썸네일은 공개스냅 중 최신스냅이 선택됩니다.")
    @Parameters({
            @Parameter(name = "thumnailCnt" , description = "필요한 각 앨범의 썸네일 개수를 보내주세요."),
            @Parameter(name = "targetLoginId", description = "조회할 유저의 loginId를 입력해주세요.")
    })
    @Parameter(name = "targetLoginId", description = "앨범을 조회할 유저의 loginId를 입력해주세요.")
    public ResponseEntity<CommonResponseDto<List<AlbumFindResDto>>> findAllAlbumsWithThumnail(
            final @RequestParam("targetLoginId") @NotBlank(message = "조회할 유저의 loginId를 입력해주세요.") String targetLoginId,
            final @RequestParam("thumnailCnt") @NotNull(message = "필요한 각 앨범의 썸네일 개수를 보내주세요.")Long thumnailCnt) {

        return ResponseEntity.status(HttpStatus.OK).body
                (CommonResponseDto.of("앨범목록(썸네일 포함) 조회성공", albumService.findAllAlbumsWithThumnail(targetLoginId, thumnailCnt)));
    }

    @GetMapping("/albums")
    @Operation(summary = "Album 목록 조회", description = "자신의 Album 목록(썸네일 미포함)을 조회합니다.<br>" +
                                                        "다른유저의 Album목록(썸네일 미포함)을 조회하는 요청은 없습니다.")
    public ResponseEntity<CommonResponseDto<List<AlbumFindResDto>>> findAllAlbums(
            final @AuthenticationPrincipal String reqLoginId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범목록(썸네일 미포함) 조회성공", albumService.findAllAlbums(reqLoginId)));
    }

    @PostMapping("/albums")
    @Operation(summary = "Album 생성", description = "사용자의 Album을 생성합니다.")
    public ResponseEntity<CommonResponseDto<Void>> addAlbum(
            final @AuthenticationPrincipal String reqLoginId,
            @RequestBody @Valid AlbumAddReqDto albumAddReqDto) {

        albumService.addAlbum(reqLoginId, albumAddReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("사용자의 앨범을 정상적으로 생성했습니다.", null));
    }

    @PatchMapping("/albums/{albumId}")
    @Operation(summary = "Album 이름 변경", description = "Album의 이름을 변경합니다.")
    @Parameter(name = "albumId", description = "변경할 앨범의 Id를 입력해주세요.", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> updateAlbumName(
            final @AuthenticationPrincipal String reqLoginId,
            @RequestBody @Valid AlbumUpdateReqDto albumUpdateReqDto,
            final @PathVariable Long albumId) {

        albumService.updateAlbumName(reqLoginId, albumId, albumUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범 수정완료", null));
    }

    @DeleteMapping("/albums/{albumId}")
    @Operation(summary = "Album을 삭제합니다.", description = "Album Id로 Album을 삭제합니다.<br> " +
                                            "삭제된 앨범 안에 있는 Snap은 기본앨범으로 이동됩니다.")
    @Parameter(name = "albumId", description = "삭제될 앨범의 ID를 입력해주세요")
    ResponseEntity<CommonResponseDto<Void>> deleteAlbum(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable Long albumId) {

        albumService.deleteAlbum(reqLoginId, albumId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("앨범 삭제성공", null));
    }

}
