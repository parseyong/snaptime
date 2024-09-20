package me.snaptime.profile.service;

import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.snap.dto.res.SnapFindResDto;

import java.util.List;

public interface ProfileService {

    /*
        targetUserEmail유저의 유저프로필 정보를 조회합니다.
        팔로워 팔로잉 수, 스냅 수, 유저이름, 프로필 사진을 반환합니다.

        reqEmail        : 요청자의 email
        targetUserEmail : 프로필조회할 유저의 email
    */
    UserProfileResDto findUserProfile(String reqEmail, String targetUserEmail);


    /*
        유저가 태그된 스냅을 조회합니다.

        targetUserEmail : 조회할 유저의 email
    */
    List<SnapFindResDto> findTagSnap(String targetUserEmail);

}
