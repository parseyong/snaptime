package me.snaptime.alarm.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
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
