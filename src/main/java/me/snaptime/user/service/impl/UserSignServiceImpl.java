package me.snaptime.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.album.service.AlbumService;
import me.snaptime.auth.JwtProvider;
import me.snaptime.component.CipherComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.mail.service.MailService;
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
    private final MailService mailService;
    private final CipherComponent cipherComponent;

    @Override
    public void signUp(UserAddReqDto userAddReqDto) {

        //이메일이 이미 존재하는지 확인
        if(userRepository.findByEmail(userAddReqDto.email()).isPresent()){
            throw new CustomException(ExceptionCode.DUPLICATED_EMAIL);
        }

        mailService.checkIsVerifiedEmail(userAddReqDto.email());

        //새로운 사용자 객체 생성
        User user = User.builder()
                .nickname(userAddReqDto.nickName())
                .email(userAddReqDto.email())
                .password(passwordEncoder.encode(userAddReqDto.password()))
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

        User user = userRepository.findByEmail(signInReqDto.email())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInReqDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.LOGIN_FAIL);
        }

        return SignInResDto.toDto(
                jwtProvider.addAccessToken(user.getEmail(), user.getUserId(), user.getAuthorities()),
                jwtProvider.addRefreshToken(user.getEmail(), user.getUserId(), user.getAuthorities()));

    }

    @Override
    public SignInResDto reissueTokens(HttpServletRequest request){


        String refreshToken = jwtProvider.findTokenByHeader(request);

        User user = userRepository.findByEmail(jwtProvider.findEmailByRefreshToken(refreshToken))
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // refreshToken의 유효성 검사
        if(!jwtProvider.isValidRefreshToken(refreshToken))
            throw new CustomException(ExceptionCode.TOKEN_INVALID);

        // refreshToken과 accessToken 재발급
        return SignInResDto.toDto(
                jwtProvider.addAccessToken(user.getEmail(), user.getUserId(), user.getAuthorities()),
                jwtProvider.addRefreshToken(user.getEmail(), user.getUserId(), user.getAuthorities()));
    }
}
