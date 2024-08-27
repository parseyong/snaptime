package me.snaptime.component.url.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlComponentImpl implements UrlComponent {

    private final HttpServletRequest request;

    @Override
    public String makePhotoURL(String fileName, boolean isEncrypted) {
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() +
                "/photo?fileName=" + fileName + "&isEncrypted=" + isEncrypted;
    }
}
