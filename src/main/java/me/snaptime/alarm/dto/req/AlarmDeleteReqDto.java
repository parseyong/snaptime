package me.snaptime.alarm.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.snaptime.alarm.enums.AlarmType;

public record AlarmDeleteReqDto(

        @Schema(
                example = "알림 타입",
                description = "삭제할 알림의 타입을 입력해주세요."
        )
        @NotNull(message = "삭제할 알림의 타입을 입력해주세요.")
        AlarmType alarmType
) {
}
