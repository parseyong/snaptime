package me.snaptime.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.service.AlbumService;
import me.snaptime.auth.JwtProvider;
import me.snaptime.component.CipherComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserAddReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserSignService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSignServiceImpl implements UserSignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AlbumService albumService;
    private final CipherComponent cipherComponent;

    @Override
    public void signUp(UserAddReqDto userAddReqDto) {

        //로그인 id가 이미 존재하는지 확인
        if(userRepository.findByLoginId(userAddReqDto.loginId()).isPresent()){
            throw new CustomException(ExceptionCode.DUPLICATED_LOGIN_ID);
        }

        //새로운 사용자 객체 생성
        User user = User.builder()
                .nickname(userAddReqDto.nickName())
                .loginId(userAddReqDto.loginId())
                .password(passwordEncoder.encode(userAddReqDto.password()))
                .email(userAddReqDto.email())
                .birthDay(userAddReqDto.birthDay())
                .profilePhotoName("default.png")
                .secretKey(cipherComponent.generateAESKey())
                .build();

        userRepository.save(user);

        // 기본앨범 생성
        albumService.addBasicAlbum(user);
    }

    @Override
    public SignInResDto signIn(SignInReqDto signInReqDto) {

        User user = userRepository.findByLoginId(signInReqDto.loginId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInReqDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.LOGIN_FAIL);
        }

        return SignInResDto.builder()
                .accessToken(jwtProvider.addAccessToken(user.getLoginId(), user.getUserId(), user.getAuthorities()))
                .refreshToken(jwtProvider.addRefreshToken(user.getLoginId(), user.getUserId(), user.getAuthorities()))
                .build();
    }

    @Override
    public SignInResDto reissueTokens(HttpServletRequest request){


        String refreshToken = jwtProvider.findTokenByHeader(request);

        User user = userRepository.findByLoginId(jwtProvider.findLoginIdByRefreshToken(refreshToken))
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // refreshToken의 유효성 검사
        if(!jwtProvider.isValidRefreshToken(refreshToken))
            throw new CustomException(ExceptionCode.TOKEN_INVALID);

        // refreshToken과 accessToken 재발급
        return SignInResDto.builder()
                .accessToken(jwtProvider.addAccessToken(user.getLoginId(), user.getUserId(), user.getAuthorities()))
                .refreshToken(jwtProvider.addRefreshToken(user.getLoginId(), user.getUserId(), user.getAuthorities()))
                .build();
    }
}
