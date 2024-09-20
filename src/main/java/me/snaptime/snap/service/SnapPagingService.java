package me.snaptime.snap.service;

import me.snaptime.snap.dto.res.SnapFindPagingResDto;

public interface SnapPagingService {

    /*
        자신이 팔로우한 유저들의 스냅을 최신순으로 불러옵니다.
        커뮤니티 기능이므로 10개씩 페이징처리를 합니다.

        reqEmail : 요청자의 email
        pageNum  : 조회할 스냅커뮤니티 페이지번호
    */
    SnapFindPagingResDto findSnapPage(String reqEmail, Long pageNum);
}
