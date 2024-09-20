package me.snaptime.mail.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.RedisPrefix;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.mail.domain.MailAuthInfo;
import me.snaptime.mail.repository.MailAuthRepository;
import me.snaptime.mail.service.MailService;
import me.snaptime.util.AuthMessageGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService {

    private final MailAuthRepository mailAuthRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.naver.email}")
    private String naverEmail;

    @Override
    @Transactional
    public void sendAuthMessage(String reqEmail) {

        // 8자리의 랜덤문자열 생성
        String authMessage = AuthMessageGenerator.generateAuthMessage(8);

        try {
            sendMessageToMail(reqEmail,authMessage);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.MAIL_SEND_FAIL);
        }

        mailAuthRepository.save(new MailAuthInfo(RedisPrefix.EMAIL_AUTH+reqEmail,authMessage,false));
    }

    @Override
    public void verifyAuthMessage(String reqEmail, String authMessage) {
        Optional<MailAuthInfo> mailAuthInfo = mailAuthRepository.findEmailInfoByEmail(RedisPrefix.EMAIL_AUTH+reqEmail);

        if(mailAuthInfo.isEmpty() || !mailAuthInfo.get().getAuthMessage().equals(authMessage))
            throw new CustomException(ExceptionCode.MAIL_AUTH_FAIL);

        mailAuthRepository.save(new MailAuthInfo(RedisPrefix.EMAIL_AUTH+reqEmail,authMessage,true));
    }

    @Override
    public void checkIsVerifiedEmail(String reqEmail) {
        Optional<MailAuthInfo> mailAuthInfo = mailAuthRepository.findEmailInfoByEmail(RedisPrefix.EMAIL_AUTH+reqEmail);

        if(mailAuthInfo.isEmpty() || !mailAuthInfo.get().isVerify())
            throw new CustomException(ExceptionCode.MAIL_NOT_AUTH);
    }

    private void sendMessageToMail(String reqEmail, String authMessage) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true,"utf-8");

        String msgg = "";
        msgg += "<div class='main-container' style='border: 2px solid #2196F3; padding: 20px; border-radius: 10px; font-family: Arial, sans-serif; margin: 20px;'>";
        msgg += "<h1 style='color: #2196F3;'>SnapTime 입니다.</h1>";
        msgg += "<hr style='border: none; border-top: 2px solid #2196F3;'>";
        msgg += "<div class='instruction-block' style='background-color: #e3f2fd; padding: 15px; font-size: 20px; font-weight: bold; color: #0d47a1; border: 1px solid #90caf9; border-radius: 5px; margin-top: 20px; text-align: center;'>";
        msgg += "<p>아래 코드를 복사해 입력해주세요.</p>";
        msgg += "<p>감사합니다.</p>";
        msgg += "</div>";
        msgg += "<div class='message-container' style='text-align: center; border: 1px solid #ddd; padding: 20px; background-color: #f9f9f9;'>";
        msgg += "<div class='code-block' style='border: 2px solid #2196F3; padding: 15px; background-color: #f0f8ff; border-radius: 5px;'>";
        msgg += "<h3 style='color: #2196F3;'>회원 가입 인증 코드입니다.</h3>";
        msgg += "<div class='code' style='font-size: 150%; color: #333; margin-top: 10px;'>";
        msgg += "CODE : <strong style='color: #E91E63;'>"+authMessage+"</strong>"; // 여기에 verificationCode 값을 넣으세요
        msgg += "</div>";
        msgg += "</div>";
        msgg += "</div>";
        msgg += "</div>";

        helper.setFrom(naverEmail);
        helper.setTo(reqEmail);
        helper.setSubject("회원 가입 인증 코드");
        helper.setText(msgg,true);

        javaMailSender.send(message);
    }
}
