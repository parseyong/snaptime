package me.snaptime.exception.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(403);
        Gson gson = new Gson();

        log.info("권한이 없는 페이지입니다.");
        CommonResponseDto commonResponseDto = CommonResponseDto.of("권한이 없습니다",null);

        response.getWriter().write(gson.toJson(commonResponseDto));

    }
}
