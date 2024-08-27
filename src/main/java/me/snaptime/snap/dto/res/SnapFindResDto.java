package me.snaptime.snap.dto.res;

import lombok.Builder;
import me.snaptime.snap.domain.Snap;

import java.time.LocalDateTime;

@Builder
public record SnapFindResDto(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate

) {
    public static SnapFindResDto toDto(Snap snap, String snapPhotoURL){
        return SnapFindResDto.builder()
                .snapId(snap.getSnapId())
                .oneLineJournal(snap.getOneLineJournal())
                .snapCreatedDate(snap.getCreatedDate())
                .snapModifiedDate(snap.getLastModifiedDate())
                .snapPhotoURL(snapPhotoURL)
                .build();
    }
}