package ru.aston.CRUDSpringApp.util;

import java.time.LocalDateTime;

/**
 * Класс {@link UserErrorResponse} предназначен для формирования удобочитаемых сообщений об ошибках в теле ответа.
 */
public class UserErrorResponse {
    /**
     * Поле {@link #message} содержит описание возникшей ошибки
     */
    private String message;
    /**
     * Поле {@link #timestamp} содержит время возникновения ошибки
     */
    private LocalDateTime timestamp;

    public UserErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
