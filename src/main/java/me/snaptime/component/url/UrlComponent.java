package me.snaptime.component.url;

public interface UrlComponent {

    /*
        snap photo에 접근하기 위한 url을 생성합니다.
    */
    String makePhotoURL(String fileName, boolean isEncrypted);

    /*
        user profile photo에 접근하기 위한 url을 생성합니다.
    */
    String makeProfileURL(Long id);
}
