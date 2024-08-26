package me.snaptime.component.cipher;

import javax.crypto.SecretKey;

public interface CipherComponent {


    /*
        secretKey로 파일데이터 암호화합니다.
        암호화된 byte[]를 반환합니다.

        secretKey : 암호화에 쓰이는 개인키
        fileBytes : 암호화할 사진의 바이트데이터
    */
    byte[] encryptData(SecretKey secretKey, byte[] fileBytes);

    /*
        secretKey로 파일데이터 복호화합니다.
        복호화된 byte[]를 반환합니다.

        secretKey : 복호화에 쓰이는 개인키
        fileBytes : 복호화할 사진의 바이트데이터
    */
    byte[] decryptData(SecretKey secretKey, byte[] fileBytes);

}
