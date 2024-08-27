package me.snaptime.component.cipher.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.cipher.CipherComponent;
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
public class CipherComponentImpl implements CipherComponent {

    public SecretKey generateAESKey() {

        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e){
            throw new CustomException(ExceptionCode.ADD_SECRET_KEY_FAIL);
        }
    }

    @Override
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

    @Override
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
