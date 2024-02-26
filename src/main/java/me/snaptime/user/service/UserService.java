package me.snaptime.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.config.security.JwtProvider;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.SignInRequestDto;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResponseDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserResponseDto.toDto(user);
    }

    @Transactional
    public UserResponseDto signUp(UserRequestDto userRequestDto){
        log.info("[signUp] 회원 가입 정보를 전달합니다.");

        User user = User.builder()
                .Id(null)
                .name(userRequestDto.name())
                .loginId(userRequestDto.loginId())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .email(userRequestDto.email())
                .birthDay(userRequestDto.birthDay())
                //단일 권한을 가진 리스트 생성, 하나의 요소를 가진 불변의 리스트 생성
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        return UserResponseDto.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto){
        log.info("[signIn] signDataHandler로 회원 정보 요청");
        User user = userRepository.findByLoginId(signInRequestDto.loginId()).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
        log.info("[signIn] loginId : {}",signInRequestDto.loginId());

        log.info("[signIn] loginId에 해당하는 유저의 비밀번호와 입력 한 비밀번호를 비교합니다.");
        if(!passwordEncoder.matches(signInRequestDto.password(),user.getPassword())){
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        log.info("[signIn] 비밀번호가 일치합니다.");
        String accessToken = jwtProvider.createAccessToken(user.getLoginId(),user.getRoles());

        log.info("[signIn] SignInResultDto 객체를 생성합니다.");
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .accessToken(accessToken)
                .build();

        return signInResponseDto;
    }

    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
        userRepository.deleteById(user.getId());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto){

        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (userUpdateDto.name() !=null && !userUpdateDto.name().isEmpty() && !userUpdateDto.name().equals("string")){
            user.updateUserName(userUpdateDto.name());
        }

        if (userUpdateDto.loginId() !=null && !userUpdateDto.loginId().isEmpty()&& !userUpdateDto.loginId().equals("string")){
            user.updateUserLoginId(userUpdateDto.loginId());
        }

        if (userUpdateDto.email() !=null && !userUpdateDto.email().isEmpty()&& !userUpdateDto.email().equals("string")){
            user.updateUserEmail(userUpdateDto.email());
        }

        if (userUpdateDto.birthDay() !=null && !userUpdateDto.birthDay().isEmpty()&& !userUpdateDto.birthDay().equals("string")){
            user.updateUserBirthDay(userUpdateDto.birthDay());
        }

        return UserResponseDto.toDto(userRepository.save(user));
    }

}
