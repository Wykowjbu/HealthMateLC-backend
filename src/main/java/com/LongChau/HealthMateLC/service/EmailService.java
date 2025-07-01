package com.LongChau.HealthMateLC.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP của bạn");
        message.setText("Mã OTP của bạn là: " + otp + ". Hết hạn sau 5 phút.");
        message.setFrom("Hoangtrinh240705@gmail.com"); // Thay bằng email thực tế
        mailSender.send(message);
    }
}
