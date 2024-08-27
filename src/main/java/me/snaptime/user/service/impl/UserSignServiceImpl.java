package me.snaptime.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.album.service.AlbumService;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.redis.RefreshToken;
import me.snaptime.jwt.redis.RefreshTokenRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserSignService;
import me.snaptime.util.CipherUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserSignServiceImpl implements UserSignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AlbumService albumService;

    @Override
    public UserFindResDto signUp(UserReqDto userReqDto) {

        //로그인 id가 이미 존재하는지 확인
        if(userRepository.findByLoginId(userReqDto.loginId()).isPresent()){
            throw new CustomException(ExceptionCode.DUPLICATED_LOGIN_ID);
        }

        String fileName = "default.png";
        String filePath =  "/test_resource/" + fileName;

        //새로운 사용자 객체 생성
        User user = User.builder()
                .nickname(userReqDto.name())
                .loginId(userReqDto.loginId())
                .password(passwordEncoder.encode(userReqDto.password()))
                .email(userReqDto.email())
                .birthDay(userReqDto.birthDay())
                .profilePhotoPath(filePath)
                .profilePhotoName(fileName)
                .secretKey(CipherUtil.generateAESKey())
                .build();

        // NonClassification 앨범 생성
        albumService.addBasicAlbum(user);

        return UserFindResDto.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public SignInResDto signIn(SignInReqDto signInReqDto) {
        User user = userRepository.findByLoginId(signInReqDto.loginId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInReqDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.LOGIN_FAIL);
        }
        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getLoginId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getLoginId());
        refreshTokenRepository.save(new RefreshToken(user.getUserId(),refreshToken));

        return SignInResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResDto reissueAccessToken(HttpServletRequest request){

        String token = jwtProvider.getAuthorizationToken(request);
        Long userId = jwtProvider.getUserId(token);

        RefreshToken refreshToken = refreshTokenRepository.findById(userId).orElseThrow(()-> new CustomException(ExceptionCode.TOKEN_NOT_FOUND));

        if(!refreshToken.getRefreshToken().equals(token)) {
            throw new CustomException(ExceptionCode.TOKEN_INVALID);
        }

        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        String newAccessToken = jwtProvider.createAccessToken(userId,user.getLoginId());
        String newRefreshToken = jwtProvider.createRefreshToken(userId,user.getLoginId());

        SignInResDto signInResDto = SignInResDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));

        return signInResDto;
    }

}
