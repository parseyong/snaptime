package me.snaptime.photo.service;

public interface PhotoService {

    /*
        사진(프로필,스냅)을 가져와 반환합니다. URL이 아닌 사진의 byte[]를 반환합니다.
        암호화된 사진인 경우 인증을 한 뒤 복호화하여 반환합니다.
        기본적으로는 인증토큰없이 사진조회가 가능하지만 암호화된 사진의 경우에는 인증토큰이 필요합니다.

        reqLoginId  : 요청자의 loginId
        fileName    : 조회할 사진의 이름
        isEncrypted : 암호화 여부
    */
    byte[] findPhoto(String reqLoginId, String fileName, boolean isEncrypted);
}
