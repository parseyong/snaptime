package me.snaptime.snap.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import java.time.LocalDateTime;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindSnapPagingResDto(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String loginId,
        String profilePhotoURL,
        String userName

) {
    public static FindSnapPagingResDto toDto(Tuple result, String profilePhotoURL, String snapPhotoURL){
        return FindSnapPagingResDto.builder()
                .snapId(result.get(snap.id))
                .oneLineJournal(String.valueOf(result.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(result.get(snap.createdDate))
                .snapModifiedDate(result.get(snap.lastModifiedDate))
                .loginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(user.name))
                .build();
    }
}
