package me.snaptime.component.encryption;

import javax.crypto.SecretKey;

public interface CipherComponent {


    /*
    * encryptData
    * encryption entity와 byte[]를 인자로 받아 byte[]를 encryption entity의 field인 secretKey로
    * 암호화 한 후 암호화 한 데이터를 반환하는 메소드
    * */
    byte[] encryptData(SecretKey secretKey, byte[] fileBytes);

    /*
     * decryptData
     * encryption entity와 byte[]를 인자로 받아 byte[]를 encryption entity의 field인 secretKey로
     * 복호화 한 후 복호화 한 데이터를 반환하는 메소드
     * */
    byte[] decryptData(SecretKey secretKey, byte[] fileBytes);

}
