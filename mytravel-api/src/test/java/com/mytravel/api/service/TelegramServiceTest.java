package com.mytravel.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("TelegramService Unit Tests")
class TelegramServiceTest {

    private TelegramService telegramService;

    @BeforeEach
    void setUp() {
        telegramService = new TelegramService();
    }

    @Test
    @DisplayName("sendOtpNotification and sendMessage - Missing config should safely skip without throwing")
    void missingConfig_SkipsSafely() {
        ReflectionTestUtils.setField(telegramService, "botToken", "");
        ReflectionTestUtils.setField(telegramService, "chatId", "");

        assertDoesNotThrow(() -> telegramService.sendOtpNotification("test@mytravel.com", "123456"));
        assertDoesNotThrow(() -> telegramService.sendMessage("Test message"));
    }

    @Test
    @DisplayName("sendMessage - With invalid token catches exception gracefully")
    void withConfig_CatchesHttpExceptions() {
        ReflectionTestUtils.setField(telegramService, "botToken", "invalid_test_bot_token");
        ReflectionTestUtils.setField(telegramService, "chatId", "123456789");

        // Should not throw out of method because error is logged inside try-catch
        assertDoesNotThrow(() -> telegramService.sendMessage("Hello Telegram"));
        assertDoesNotThrow(() -> telegramService.sendOtpNotification("user@domain.com", "654321"));
    }
}
