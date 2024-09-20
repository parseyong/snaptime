package me.snaptime.user.service;

import me.snaptime.user.dto.req.UserDeleteReqDto;
import me.snaptime.user.dto.req.UserUpdatePasswordReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindMyPageResDto;
import me.snaptime.user.dto.res.UserFindPagingResDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    /*
        나의 유저정보를 조회합니다.

        reqEmail : 요청자의 이메일
    */
    UserFindMyPageResDto findUserMyPage(String reqEmail);

    /*
        키워드로 유저를 검색합니다.
        유저의 닉네임 또는 이메일중 키워드로 시작하는 유저를 페이징처리 후 조회합니다.

        searchKeyword : 검색 키워드
        pageNum       : 페이지 번호
    */
    UserFindPagingResDto searchUserPaging(String searchKeyword, Long pageNum);

    /*
        유저정보(비밀번호 제외)를 수정합니다.

        reqEmail         : 요청자의 email
        userUpdateReqDto : 유저정보 수정정보가 담긴 요청dto
    */
    void updateUser(String reqEmail, UserUpdateReqDto userUpdateReqDto);

    /*
        유저의 비밀번호를 변경합니다.
        이전과 같은 비밀번호로 변경할 수 없습니다.

        reqEmail                 : 요청자의 email
        userUpdatePasswordReqDto : 새로운 비밀번호가 담긴 dto
    */
    void updatePassword(String reqEmail, UserUpdatePasswordReqDto userUpdatePasswordReqDto);

    /*
        유저의 프로필사진을 수정합니다.

        reqEmail      : 요청자의 email
        multipartFile : 수정할 사진
    */
    void updateProfilePhoto(String reqEmail, MultipartFile multipartFile);

    /*
        유저를 탈퇴합니다.
        비밀번호가 틀리면 예외를 반환합니다.

        reqEmail           : 요청자의 email
        userDeleteReqDto   : 유저의 비밀번호가 담긴 dto
    */
    void deleteUser(String reqEmail, UserDeleteReqDto userDeleteReqDto);


}
