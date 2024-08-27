package me.snaptime.component.url;

public interface UrlComponent {

    /*
        photo에 접근하기 위한 url을 생성합니다.
        photoUrl을 반환합니다.

        fileName    : 접근할 파일의 이름
        isEncrypted : 암호화된 파일인지 여부
    */
    String makePhotoURL(String fileName, boolean isEncrypted);
}
