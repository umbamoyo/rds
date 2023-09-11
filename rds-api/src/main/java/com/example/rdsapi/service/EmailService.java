package com.example.rdsapi.service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.Option;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final Random random;
    private final EmailCodeRepository emailCodeRepository;

    public static int leftEmailAuthenticationCodeLimit = 48;    // 0
    public static int rightEmailAuthenticationCodeLimit = 122;  // z
    public static int emailAuthenticationCodeLength = 10;
    public static Long expireSignUpEmailAuthentication = 5L;



    @Async
    @Transactional
    public void sendSignUpAuthenticationMail(EmailCodeDto dto){

        String code = makeEmailAuthenticationCode();

        // 이메일 인증 코드 재 전송 요청 여부 파악
        Optional<EmailCode> emailCodeEntity = emailCodeRepository.findById(dto.userId());
        emailCodeEntity.ifPresentOrElse(
                emailCode -> {
                    emailCodeRepository.delete(emailCode);
                    emailCodeRepository.save(EmailCode.of(dto.userId(), code));
                },
                () -> emailCodeRepository.save(EmailCode.of(dto.userId(), code))
        );


        final MimeMessagePreparator preparator = message -> {
            final MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setSubject("RDS 이메일 인증코드 입니다.");
            helper.setText(makeMessage(code), true);
            helper.setFrom(new InternetAddress("sdj799@gmail.com","RDS-noReply"));
            helper.setTo(dto.userId());
        };

        try{
            emailSender.send(preparator);
        }catch (MailException e){
            throw new GeneralException(ErrorCode.EMAIL_EXCEPTION);
        }
    }



    //난수의 범위: 10글자, 소문자 + 숫자 결합
    public String makeEmailAuthenticationCode() {
        return random.ints(leftEmailAuthenticationCodeLimit,rightEmailAuthenticationCodeLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(emailAuthenticationCodeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String makeMessage(String code){
        String text = "";
        text += "<div style='margin:20px;'>";
        text += "홈페이지를 방문해 주셔서 감사합니다.";
        text += "<br>";
        text += "<p>아래 코드를 복사해 입력해주세요<p>";
        text += "<br>";
        text += "<p>감사합니다.<p>";
        text += "<br>";
        text += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        text += "<div style='font-size:130%'>";
        text += "CODE : <strong>";
        text += code +"</strong><div><br/> ";
        text += "</div>";

        return text;
    }

    // 이메일 인증코드가 일치하는지 확인
    @Transactional
    public boolean authenticate(EmailCodeDto dto){
        EmailCode emailCodeEntity = emailCodeRepository.findById(dto.userId())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));


        // 인증 유효시간을 초과한 경우
        if(emailCodeEntity.getCreatedAt().plusMinutes(expireSignUpEmailAuthentication).before(LocalDateTime.now())){
            throw new GeneralException(ErrorCode.EMAIL_AUTHENTICATION_CODE_EXPIRE);
        }

        // 인증 코드가 일치하지 않는 경우
        if(!emailCodeEntity.getCode().equals(dto.code())){
            throw new GeneralException(ErrorCode.EMAIL_AUTHENTICATION_NOT_INVALID);
        }

        emailCodeRepository.delete(emailCodeEntity);
        return true;
    }

}
