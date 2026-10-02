package com.mytravel.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:${spring.mail.username}}")
  private String fromAddress;

  @Async
  public void sendOtpEmail(String toEmail, String otpCode) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromAddress);
      helper.setTo(toEmail);
      helper.setSubject("Mã OTP khôi phục mật khẩu - MyTravel");

      String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
          "<h2 style=\"color: #2c3e50; text-align: center;\">Khôi phục mật khẩu MyTravel</h2>" +
          "<p>Xin chào,</p>" +
          "<p>Bạn đã yêu cầu khôi phục mật khẩu tài khoản MyTravel. Mã OTP của bạn là:</p>" +
          "<div style=\"text-align: center; margin: 25px 0;\">" +
          "<span style=\"font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #3498db; background-color: #f4f6f7; padding: 10px 20px; border-radius: 6px;\">" +
          otpCode + "</span>" +
          "</div>" +
          "<p>Mã OTP này có hiệu lực trong vòng <strong>10 phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
          "<br>" +
          "<p style=\"font-size: 12px; color: #7f8c8d; text-align: center;\">Email này được gửi tự động từ hệ thống MyTravel. Vui lòng không phản hồi.</p>" +
          "</div>";

      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Sent OTP email successfully to {}", toEmail);
    } catch (Exception e) {
      log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
    }
  }
}
