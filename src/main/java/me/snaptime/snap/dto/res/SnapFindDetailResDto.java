package me.snaptime.snap.dto.res;

import com.querydsl.core.Tuple;
import lombok.AccessLevel;
import lombok.Builder;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.dto.res.TagUserFindResDto;

import java.time.LocalDateTime;
import java.util.List;

import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;


@Builder(access = AccessLevel.PRIVATE)
public record SnapFindDetailResDto(

        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String writerEmail,
        String profilePhotoURL,
        String writerUserName,
        List<TagUserFindResDto> tagUserFindResDtos,
        Long likeCnt,
        boolean isLikedSnap
) {
    // tuple형태의 snap정보를 받을 떄 사용
    public static SnapFindDetailResDto toDto(Tuple tuple, String profilePhotoURL, String snapPhotoURL,
                                             List<TagUserFindResDto> tagUserFindResDtos, Long likeCnt, boolean isLikedSnap){

        return SnapFindDetailResDto.builder()
                .snapId(tuple.get(snap.snapId))
                .oneLineJournal(String.valueOf(tuple.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(tuple.get(snap.createdDate))
                .snapModifiedDate(tuple.get(snap.lastModifiedDate))
                .writerEmail(tuple.get(user.email))
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.nickname))
                .tagUserFindResDtos(tagUserFindResDtos)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }

    // Snap객체로 snap정보를 받을 떄 사용
    public static SnapFindDetailResDto toDto(Snap snap, String profilePhotoURL, String snapPhotoURL,
                                             List<TagUserFindResDto> tagUserFindResDtos, Long likeCnt, boolean isLikedSnap){

        return SnapFindDetailResDto.builder()
                .snapId(snap.getSnapId())
                .oneLineJournal(snap.getOneLineJournal())
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(snap.getCreatedDate())
                .snapModifiedDate(snap.getLastModifiedDate())
                .writerEmail(snap.getWriter().getEmail())
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(snap.getWriter().getNickname())
                .tagUserFindResDtos(tagUserFindResDtos)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }


}
