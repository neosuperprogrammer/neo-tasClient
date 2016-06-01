package com.tionsoft.test.automation.tools;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class EmailSender {
 
	public void sendEmail(String emailFrom, String emailTo, String subject, String content) throws AddressException, MessagingException{
         
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 무조건 true 고정
        p.put("mail.smtp.host", "owa.magnachip.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 무조건 true 고정
        p.put("mail.smtp.port", "25");                 // gmail 포트
        p.put("mail.debug", "false");           
        Authenticator auth = new MyAuthentication();
         
        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);
         
        try {
            //편지보낸시간
            msg.setSentDate(new Date());
             
            InternetAddress from = new InternetAddress();
            from = new InternetAddress(emailFrom);
             
            // 이메일 발신자
            msg.setFrom(from);
             
            // 이메일 수신자
            InternetAddress to = new InternetAddress(emailTo);
            msg.setRecipient(Message.RecipientType.TO, to);
             
            // 이메일 제목
            msg.setSubject(subject, "UTF-8");
             
            // 이메일 내용 
            msg.setText(content, "UTF-8");
             
            // 이메일 헤더 
            msg.setHeader("content-Type", "text/html");
            
            msg.saveChanges();
            
            //메일보내기
            Transport.send(msg);
        } catch (AddressException addr_e) {
            //addr_e.printStackTrace();
			throw addr_e;
        } catch (MessagingException msg_e) {
            //msg_e.printStackTrace();
			throw msg_e;
        }
    }
}
 
class MyAuthentication extends Authenticator {
      
    PasswordAuthentication pa;
    
    public MyAuthentication(){
        String id = "user02";       // 구글 ID
        String pw = "123qwe!";  // 구글 비밀번호
 
        // ID와 비밀번호를 입력한다.
        pa = new PasswordAuthentication(id, pw);
    }
 
    // 시스템에서 사용하는 인증정보
    public PasswordAuthentication getPasswordAuthentication() {
        return pa;
    }
}