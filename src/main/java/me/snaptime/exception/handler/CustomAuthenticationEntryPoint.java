package me.snaptime.exception.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(401);
        Gson gson = new Gson();

        log.info("인증에 실패하였습니다.");
        CommonResponseDto commonResponseDto = CommonResponseDto.of("인증에 실패하였습니다",null);
        response.getWriter().write(gson.toJson(commonResponseDto));
    }
}
