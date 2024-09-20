package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserAddReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserFindMyPageResDto;
import me.snaptime.user.service.UserSignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name ="[Sign] Sign API")
public class UserSignController {

    private final UserSignService userSignService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.<br>" +
                                    "<br> 회원가입이 완료되면 기본앨범이 자동으로 생성되며" +
                                    "<br> 프로필사진은 기본프로필로 자동지정됩니다.")
    public ResponseEntity<CommonResponseDto<UserFindMyPageResDto>> signUp(@RequestBody @Valid UserAddReqDto userAddReqDto){

        userSignService.signUp(userAddReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("유저 회원가입을 성공적으로 완료하였습니다.", null));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    public ResponseEntity<CommonResponseDto<SignInResDto>> signIn(@RequestBody @Valid SignInReqDto signInReqDto){
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("로그인 성공", userSignService.signIn(signInReqDto)));
    }

    @PostMapping("/reissue")
    @Operation(summary = "엑세스 토큰 재발급", description = "RefreshToken 을 통해 AccessToken과 "+
                                            "RefreshToken을 재발급받습니다.<br>" +
                                            "기존 AccessToken자리에 refreshToken을 넣어서 보내주세요.")
    public ResponseEntity<CommonResponseDto<SignInResDto>> reissue(HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("토큰 재발급 성공", userSignService.reissueTokens(request)));
    }
}
