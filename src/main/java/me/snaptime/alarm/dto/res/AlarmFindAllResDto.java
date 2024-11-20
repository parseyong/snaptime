package me.snaptime.alarm.dto.res;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record AlarmFindAllResDto(

        List<AlarmFindResDto> notReadAlarmFindResDtos,
        List<AlarmFindResDto> readAlarmFindResDtos

) {
    public static AlarmFindAllResDto toDto(List<AlarmFindResDto> notReadAlarmFindResDtos, List<AlarmFindResDto> readAlarmFindResDtos){

        return AlarmFindAllResDto.builder()
                .notReadAlarmFindResDtos(notReadAlarmFindResDtos)
                .readAlarmFindResDtos(readAlarmFindResDtos)
                .build();
    }

}
