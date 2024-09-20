package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.user.dto.req.UserDeleteReqDto;
import me.snaptime.user.dto.req.UserUpdatePasswordReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindMyPageResDto;
import me.snaptime.user.dto.res.UserFindPagingResDto;
import me.snaptime.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name ="[User] User API")
public class UserController {

    private final UserService userService;

    @GetMapping("/users/my")
    @Operation(summary = "자신의 유저 정보 조회",description = "자신의 유저 정보를 조회합니다.")
    public ResponseEntity<CommonResponseDto<UserFindMyPageResDto>> findUserMyPage(
            final @AuthenticationPrincipal String reqEmail){

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponseDto.of("나의 유저 정보가 성공적으로 조회되었습니다.", userService.findUserMyPage(reqEmail)));
    }

    @GetMapping("/users/{pageNum}")
    @Operation(summary = "다른유저 정보 조회",description = "유저이름을 통해 유저리스트를 반환합니다.<br>"+
                                                        "email 또는 닉네임이 키워드로 시작하는 유저를 20개씩 반환합니다.<br>"+
                                                        "searchKeyword는 반드시 입력해야합니다.")
    @Parameters({
            @Parameter(name = "searchKeyword" , description = "searchKeyword를 입력해주세요", required = true, example = "검색어"),
            @Parameter(name = "pageNum", description = "페이지번호를 입력해주세요,", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto<UserFindPagingResDto>> searchUserPaging(
            final @PathVariable(name = "pageNum") Long pageNum,
            final @RequestParam(name = "searchKeyword") @NotBlank(message = "검색어를 입력해주세요.") String searchKeyword){

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponseDto.of("유저 검색이 완료되었습니다.", userService.searchUserPaging(searchKeyword, pageNum)));
    }

    @PatchMapping("/users")
    @Operation(summary = "유저 정보 수정",description = "자신의 유저의 정보를 수정합니다. " +
                                        "비밀번호를 제외한 nickName,birthDay정보를 수정합니다.<br>" +
                                        "변경을 원하지 않는 값에대해서는 기존값을 입력해주세요.")
    public ResponseEntity<CommonResponseDto<UserFindMyPageResDto>> updateUser(
            final @AuthenticationPrincipal String reqEmail,
            @RequestBody @Valid UserUpdateReqDto userUpdateReqDto){

        userService.updateUser(reqEmail, userUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("유저 정보 수정이 성공적으로 완료되었습니다.", null));
    }

    @PatchMapping("/users/password")
    @Operation(summary = "유저 비밀번호 수정",description = "해당 유저의 비밀번호를 수정합니다.<br>" +
                                            "기존 비밀번호와 같은 비밀번호 입력 시 예외가 발생합니다.")
    public ResponseEntity<CommonResponseDto<Void>> updatePassword(
            final @AuthenticationPrincipal String reqEmail,
            @RequestBody @Valid UserUpdatePasswordReqDto userUpdatePasswordReqDto) {

        userService.updatePassword(reqEmail, userUpdatePasswordReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("유저 비밀번호 수정이 성공적으로 완료되었습니다.", null));
    }

    @PutMapping(value = "/users/profile/photo", consumes = MULTIPART_FORM_DATA)
    @Operation(summary = "프로필 사진 수정",description = "유저의 프로필 사진을 수정 합니다.")
    public ResponseEntity<?> updateProfilePhoto(
            final @AuthenticationPrincipal String reqEmail,
            @RequestParam @NotNull(message = "사진을 입력해주세요") MultipartFile multipartFile) {

        userService.updateProfilePhoto(reqEmail, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("프로필 사진 수정이 완료되었습니다.", null));
    }

    @DeleteMapping("/users")
    @Operation(summary = "유저 삭제",description = "유저 탈퇴를 합니다. 비밀번호가 맞아야만 탈퇴가 가능합니다.")
    public ResponseEntity<CommonResponseDto<Void>> deleteUser(
            final @AuthenticationPrincipal String reqEmail,
            @RequestBody @Valid UserDeleteReqDto userDeleteReqDto){

        userService.deleteUser(reqEmail, userDeleteReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("유저 삭제가 성공적으로 완료되었습니다.", null));
    }

}
