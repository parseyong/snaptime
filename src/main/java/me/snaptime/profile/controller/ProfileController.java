package me.snaptime.profile.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.profile.service.ProfileService;
import me.snaptime.snap.dto.res.SnapFindResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name ="[Profile] Profile API")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/users/{targetLoginId}/profile/info")
    @Operation(summary = "유저 프로필정보 조회", description = "유저의 프로필정보(스냅 수, 친구 수, 이름, 프로필사진)를 조회합니다.")
    @Parameter(name = "targetLoginId", description = "프로필조회할 유저의 loginId", required = true)
    public ResponseEntity<CommonResponseDto<UserProfileResDto>> findUserProfile(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("targetLoginId") String targetLoginId){

        UserProfileResDto userProfileResDto = profileService.findUserProfile(reqLoginId, targetLoginId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("유저의 프로필정보 조회 성공", userProfileResDto));
    }

    @GetMapping("/users/{targetLoginId}/profile/tag-snap")
    @Operation(summary = "유저가 태그된 스냅조회", description = "targetUser가 태그된 스냅을 조회합니다.<br>" +
                                                "비공개로 전환된 스냅은 조회가 안되며 스냅은 최신순으로 정렬됩니다.")
    @Parameter(name = "targetLoginId", description = "조회할 유저의 loginId", required = true)
    public ResponseEntity<CommonResponseDto<List<SnapFindResDto>>> findTagSnap(
            final @PathVariable("targetLoginId") String targetLoginId){

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponseDto.of("유저가 태그된 Snap조회 성공", profileService.findTagSnap(targetLoginId)));
    }
}
