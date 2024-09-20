package me.snaptime.util;

import java.security.SecureRandom;

public class AuthMessageGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // authMessageLength길이의 랜덤 인증메시지를 생성합니다.
    public static String generateAuthMessage(int authMessageLength) {
        StringBuilder sb = new StringBuilder(authMessageLength);
        for (int i = 0; i < authMessageLength; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}