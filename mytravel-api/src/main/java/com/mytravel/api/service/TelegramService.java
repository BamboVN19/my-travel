package com.mytravel.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TelegramService {

  private final RestClient restClient;

  @Value("${telegram.bot.token:}")
  private String botToken;

  @Value("${telegram.bot.chat-id:}")
  private String chatId;

  public TelegramService() {
    this.restClient = RestClient.create();
  }

  @Async
  public void sendOtpNotification(String email, String otpCode) {
    if (botToken == null || botToken.isBlank() || chatId == null || chatId.isBlank()) {
      log.info("Telegram configuration missing (bot token or chat-id). Skipping Telegram OTP notification.");
      return;
    }

    String message = String.format(
        "<b>[MyTravel] Mã OTP Khôi Phục Mật Khẩu</b>\n\n" +
        "📧 <b>Email:</b> %s\n" +
        "🔑 <b>Mã OTP:</b> <code>%s</code>\n\n" +
        "⏰ <i>Mã này có hiệu lực trong 10 phút.</i>",
        email, otpCode
    );

    sendMessage(message);
  }

  @Async
  public void sendMessage(String text) {
    if (botToken == null || botToken.isBlank() || chatId == null || chatId.isBlank()) {
      log.warn("Telegram bot token or chat ID is not configured.");
      return;
    }

    try {
      String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

      Map<String, Object> body = new HashMap<>();
      body.put("chat_id", chatId);
      body.put("text", text);
      body.put("parse_mode", "HTML");

      restClient.post()
          .uri(url)
          .body(body)
          .retrieve()
          .toBodilessEntity();

      log.info("Telegram message sent successfully to chat_id: {}", chatId);
    } catch (Exception e) {
      log.error("Failed to send Telegram message to chat_id {}: {}", chatId, e.getMessage(), e);
    }
  }
}
