package me.snaptime.profile.service;

import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.snap.dto.res.SnapFindResDto;

import java.util.List;

public interface ProfileService {

    /*
        targetLoginId의 유저프로필 정보를 조회합니다.
        팔로워 팔로잉 수, 스냅 수, 유저이름, 프로필 사진을 반환합니다.

        reqLoginId    : 요청자의 loginId
        targetLoginId : 프로필조회할 유저의 loginId
    */
    UserProfileResDto findUserProfile(String reqLoginId, String targetLoginId);


    /*
        유저가 태그된 스냅을 조회합니다.

        targetLoginId : 조회할 유저의 loginId
    */
    List<SnapFindResDto> findTagSnap(String targetLoginId);

}
