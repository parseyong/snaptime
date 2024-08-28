package me.snaptime.snapTag.service;

import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.dto.res.TagUserFindResDto;

import java.util.List;

public interface SnapTagService {

    /*
        스냅에 유저태그를 추가합니다.
        자기 자신을 태그할 경우 예외를 반환합니다,

        tagUserLoginIds : 태그될 유저의 loginId리스트
        snap            : 태그를 등록할 snap의 id
    */
    void addTagUser(List<String> tagUserLoginIds, Snap snap);

    /*
        스냅 수정 시 태그정보를 갱신합니다.
        태그된 유저정보를 조회하여 tagUserLoginIds와 비교하여 태그유저를 추가하거나 삭제합니다.

        tagUserLoginIds : 태그될 유저의 loginId리스트
        snap            : 태그를 등록할 snap의 id
    */
    void updateTagUsers(List<String> tagUserLoginIds, Snap snap);

    /*
        스냅에 태그된 유저들의 정보를 가져옵니다.

        snapId : 태그된 유저를 조회할 snap의 id
    */
    List<TagUserFindResDto> findTagUsers(Long snapId);
}
