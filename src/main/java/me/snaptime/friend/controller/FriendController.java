package me.snaptime.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.friend.dto.res.FriendFindPagingResDto;
import me.snaptime.friend.enums.FriendSearchType;
import me.snaptime.friend.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Social] Friend API")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/users/{receiverEmail}/friends/follow")
    @Operation(summary = "팔로우 요청", description = "팔로우할 유저의 email을 입력해주세요.<br>" +
                                    "요청자의 팔로잉 +1, receiverEmail의 팔로워 +1")
    @Parameter(name = "receiverEmail", description = "팔로우할 유저의 이메일을 입력해주세요", required = true, example = "tester1@naver.com")
    public ResponseEntity<CommonResponseDto<Void>> sendFollow(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("receiverEmail") String receiverEmail) {

        friendService.sendFollow(reqEmail, receiverEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("팔로우가 완료되었습니다.", null));
    }

    @DeleteMapping("/users/{deletedUserEmail}/friends/follow")
    @Operation(summary = "팔로우하는 친구삭제", description = "언팔로우 할 친구의 email을 보내주세요.<br>" +
                                            "요청자(언팔하는 유저)의 팔로잉 -1, deletedUser(언팔당하는 유저)의 팔로워 -1")
    @Parameter(name = "deletedUserEmail", description = "팔로우 삭제할 친구의 email을 입력해주세요.", required = true, example = "tester1@naver.com")
    public ResponseEntity<CommonResponseDto<Void>> unFollow(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("deletedUserEmail") String deletedUserEmail) {

        friendService.unFollow(reqEmail, deletedUserEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("팔로우삭제가 완료되었습니다.", null));
    }

    @GetMapping("/users/{targetUserEmail}/{friendSearchType}/{pageNum}")
    @Operation(summary = "팔로워/팔로잉 친구목록 조회(20개씩 반환)",
            description = "팔로워와 팔로잉중 어느 친구목록을 조회할 것인지 + 검색키워드 정보를 입력해주세요." +
                        "<br>검색키워드는 필수가 아니며 없으면 입력하지 않아도 됩니다. <br>" +
                        "검색키워드는 키워드로 시작하는 유저가 검색되며 포함된다고 모두 검색이 되지는 않습니다." +
                        "<br>스냅에 친구를 태그하기 위해 태그할 유저 조회를 할 경우 friendSearchType을 팔로잉으로 보내시면 됩니다.")
    @Parameters({
            @Parameter(name = "targetUserEmail" , description = "친구목록을 조회할 유저의 email", required = true,example = "tester1@naver.com"),
            @Parameter(name = "searchKeyword", description = "친구 검색키워드", required = false, example = "홍길동"),
            @Parameter(name = "friendSearchType", description = "검색 타입(팔로워 조회 시 FOLLOWER/팔로잉 조회 시 FOLLOWING)으로 입력해주세요.", required = true, example = "FOLLOWER"),
            @Parameter(name = "pageNum", description = "친구조회 페이지번호", required = true, example = "1")
    })
    public ResponseEntity<CommonResponseDto<FriendFindPagingResDto>> findFriendPageByUser(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable("targetUserEmail") String targetUserEmail,
            final @PathVariable("friendSearchType") FriendSearchType friendSearchType,
            final @RequestParam(name = "searchKeyword",required = false) String searchKeyword,
            final @PathVariable(name = "pageNum") Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of("친구조회가 완료되었습니다.",
                friendService.findFriendPageByUser(reqEmail, targetUserEmail, pageNum,friendSearchType,searchKeyword)));
    }

}
