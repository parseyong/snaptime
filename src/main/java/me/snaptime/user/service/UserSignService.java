package me.snaptime.user.service;

import jakarta.servlet.http.HttpServletRequest;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserAddReqDto;
import me.snaptime.user.dto.res.SignInResDto;

public interface UserSignService {

    /*
        회원가입을 진행합니다. loginId가 중복일경우 예외를 반환합니다.
        회원가입 시 개인키와 기본앨범이 생성됩니다.

        userAddDto : 회원가입정보가 담긴 dto
    */
    void signUp(UserAddReqDto userAddReqDto);

    /*
        로그인을 진행합니다.

        signInReqDto : loginId와 password가 담긴 로그인 요청dto
    */
    SignInResDto signIn(SignInReqDto signInReqDto);

    /*
        accessToken과 refreshToken을 재발급합니다.
        refreshToken이 유효하지 않다면 예외를 반환합니다.

        request : http요청 정보
    */
    SignInResDto reissueTokens(HttpServletRequest request);

}
