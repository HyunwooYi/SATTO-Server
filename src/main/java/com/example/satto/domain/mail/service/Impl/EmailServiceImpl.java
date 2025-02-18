package com.example.satto.domain.mail.service.Impl;

import com.example.satto.domain.mail.service.EmailService;
import com.example.satto.domain.users.entity.Users;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

//    // 인증번호
//    private String certificationNum;


//    @Autowired
    private final JavaMailSender emailSender;

    public static final String ePw = createKey();
    public static final String findePw = createKey();

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    private MimeMessage createMessage(String to)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();
        ((MimeMessage) message).addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msgg="";
        msgg+= "<div style='margin:15px;'>";
        msgg+= "<h1> SATTO </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>이메일 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("satto202409@gmail.com","satto"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }
    @Override
    public String sendSimpleMessage(String to)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    @Override
    public String sendFindPwMessage(String toEmail)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = findPwMessage(toEmail);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return findePw;
    }


    private MimeMessage findPwMessage(String toEmail) throws Exception {
        System.out.println("보내는 대상 : "+ toEmail);
        System.out.println("인증 번호 : "+findePw);

        MimeMessage  message = emailSender.createMimeMessage();
        ((MimeMessage) message).addRecipients(MimeMessage.RecipientType.TO, toEmail);//보내는 대상
        message.setSubject("임시 비밀번호 발급");//제목

        String msgg="";
        msgg+= "<div style='margin:15px;'>";
        msgg+= "<h1> SATTO </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 임시 비밀번호를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>임시 비밀번호 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= findePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("satto202409@gmail.com","satto"));//보내는 사람

        return message;

    }

}
