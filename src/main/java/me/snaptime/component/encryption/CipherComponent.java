package me.snaptime.component.encryption;

import javax.crypto.SecretKey;

public interface CipherComponent {


    /*
        secretKey로 파일데이터 암호화합니다.
    */
    byte[] encryptData(SecretKey secretKey, byte[] fileBytes);

    /*
        secretKey로 파일데이터 복호화합니다.
    */
    byte[] decryptData(SecretKey secretKey, byte[] fileBytes);

}
