package me.snaptime.component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlComponent {

    private final HttpServletRequest request;

    /*
        photo에 접근하기 위한 url을 생성합니다.
        photoUrl을 반환합니다.

        fileName    : 접근할 파일의 이름
        isEncrypted : 암호화된 파일인지 여부
    */
    public String makePhotoURL(String fileName, boolean isEncrypted) {
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() +
                "/photo?fileName=" + fileName + "&isEncrypted=" + isEncrypted;
    }
}
