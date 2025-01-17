package me.snaptime.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.common.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/alarms")
@RequiredArgsConstructor
@Tag(name = "[Alarm] Alarm API")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/follow/{followAlarmId}")
    @Operation(summary = "팔로우 알림 조회", description = "팔로우 알림을 읽음처리합니다.")
    @Parameter(name = "followAlarmId" , description = "followAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> readFollowAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long followAlarmId) {

        alarmService.readFollowAlarm(reqEmail, followAlarmId);
        return ResponseEntity.status(HttpStatus.OK).body( CommonResponseDto.of("팔로우알림 조회 성공", null) );
    }

    @GetMapping("/snaps/{snapAlarmId}")
    @Operation(summary = "스냅알림 조회", description = "스냅알림을 읽음처리합니다.")
    @Parameter(name = "snapAlarmId" , description = "snapAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> readSnapAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long snapAlarmId) {

        alarmService.readSnapAlarm(reqEmail, snapAlarmId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("스냅알림 조회 성공", null));
    }

    @GetMapping("/replies/{replyAlarmId}")
    @Operation(summary = "댓글알림 조회", description = "댓글알림을 읽음처리합니다.")
    @Parameter(name = "replyAlarmId" , description = "replyAlarmId를 입력해주세요", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> readReplyAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long replyAlarmId) {

        alarmService.readReplyAlarm(reqEmail, replyAlarmId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("댓글알림 조회 성공", null));
    }

    @GetMapping("/count/not-read")
    @Operation(summary = "미확인알림개수 조회", description = "확인되지 않은 알림개수를 조회합니다.")
    public ResponseEntity<CommonResponseDto<Long>> findNotReadAlarmCnt(
            final @AuthenticationPrincipal String reqEmail) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("미확인 알림개수 조회성공", alarmService.findNotReadAlarmCnt(reqEmail)));
    }

    @GetMapping
    @Operation(summary = "알림리스트 조회", description = "자신에게 온 모든 알림리스트를 조회합니다.<br>"+
                                        "읽지않은 알림을 먼저 보여주며 시간순으로 정렬하여 반환합니다.<br>"+
                                        "알림타입별로 반환되는 데이터가 다릅니다.<br>"+
                                        "각 알림타입별로 alarmId값이 부여되기 때문에 타입이 다른 알림의 경우 id값이 중복될 수 있습니다.")
    public ResponseEntity<CommonResponseDto<AlarmFindAllResDto>> findAllAlarms(
            final @AuthenticationPrincipal String reqEmail) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("알림리스트 조회성공", alarmService.findAllAlarms(reqEmail)));
    }

    @DeleteMapping ("/snaps/{snapAlarmId}")
    @Operation(summary = "스냅알림을 삭제합니다.", description = "읽음 여부와 상관없이 알림을 삭제합니다.")
    @Parameter(name = "snapAlarmId" , description = "삭제할 스냅알림의 id를 입력해주세요.", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteSnapAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long snapAlarmId) {

        alarmService.deleteSnapAlarm(reqEmail, snapAlarmId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of("스냅 알림 삭제 성공", null));
    }

    @DeleteMapping ("/follow/{followAlarmId}")
    @Operation(summary = "팔로우 알림을 삭제합니다.", description = "읽음 여부와 상관없이 알림을 삭제합니다.")
    @Parameter(name = "followAlarmId" , description = "삭제할 팔로우알림의 id를 입력해주세요.", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteFollowAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long followAlarmId) {

        alarmService.deleteFollowAlarm(reqEmail, followAlarmId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of("팔로우 알림 삭제 성공", null));
    }

    @DeleteMapping ("/replies/{replyAlarmId}")
    @Operation(summary = "알림을 삭제합니다.", description = "읽음 여부와 상관없이 알림을 삭제합니다.")
    @Parameter(name = "replyAlarmId" , description = "삭제할 댓글알림의 id를 입력해주세요.", required = true,example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteReplyAlarm(
            final @AuthenticationPrincipal String reqEmail,
            final @PathVariable Long replyAlarmId) {

        alarmService.deleteReplyAlarm(reqEmail, replyAlarmId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of("댓글 알림 삭제 성공", null));
    }
}
