package me.snaptime.mail.service;

public interface MailService {

    /*
        해당 이메일로 인증번호를 전송합니다.

        reqEmail : 요청자의 email
    */
    void sendAuthMessage(String reqEmail);

    /*
        이메일로 보낸 인증번호가 맞는지 검증합니다.

        reqEmail    : 요청자의 email
        authMessage : 확인할 인증번호
    */
    void verifyAuthMessage(String reqEmail, String authMessage);

    /*
        해당 메일이 인증완료된 메일인지 여부를 반환합니다.

        reqEmail    : 요청자의 email
    */
    void checkIsVerifiedEmail(String reqEmail);
}
