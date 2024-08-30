package me.snaptime.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CipherComponent {

    /*
        AES 개인키를 생성합니다.
    */
    public SecretKey generateAESKey() {

        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e){
            throw new CustomException(ExceptionCode.ADD_SECRET_KEY_FAIL);
        }
    }

    /*
        secretKey로 파일데이터 암호화합니다.
        암호화된 byte[]를 반환합니다.

        secretKey : 암호화에 쓰이는 개인키
        fileBytes : 암호화할 사진의 바이트데이터
    */
    public byte[] encryptData(SecretKey secretKey, byte[] fileBytes) {

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPT_FAIL);
        }
    }

    /*
        secretKey로 파일데이터 복호화합니다.
        복호화된 byte[]를 반환합니다.

        secretKey : 복호화에 쓰이는 개인키
        fileBytes : 복호화할 사진의 바이트데이터
    */
    public byte[] decryptData(SecretKey secretKey, byte[] fileBytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.DECRYPT_FAIL);
        }
    }
}
