package com.example.apptaphoa.mail;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.service.AuthService;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/** Gửi OTP qua Gmail SMTP. Tài khoản và App Password lấy từ biến môi trường. */
public class GmailOtpSender implements AuthService.OtpSender {

    @Override
    public void send(String toEmail, String code) {
        String sender = env("TAPHOA_MAIL_USER");
        String password = env("TAPHOA_MAIL_PASSWORD");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã OTP đặt lại mật khẩu");
            message.setText("Mã OTP của bạn là: " + code + "\nMã có hiệu lực trong 5 phút.", "UTF-8");
            Transport.send(message);
        } catch (Exception e) {
            throw new BusinessException("Không gửi được email OTP: " + e.getMessage());
        }
    }

    private static String env(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new BusinessException("Chưa cấu hình " + name + " để gửi email");
        }
        return value;
    }
}
