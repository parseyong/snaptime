package me.snaptime.component.cipher.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.cipher.CipherComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.util.CipherUtil;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
@Slf4j
public class CipherComponentImpl implements CipherComponent {

    @Override
    public byte[] encryptData(SecretKey secretKey, byte[] fileBytes) {
        try {
            return CipherUtil.encryptData(fileBytes, secretKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.ENCRYPT_FAIL);
        }
    }

    @Override
    public byte[] decryptData(SecretKey secretKey, byte[] fileBytes) {
        try {
            return CipherUtil.decryptData(fileBytes, secretKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.DECRYPT_FAIL);
        }
    }
}
