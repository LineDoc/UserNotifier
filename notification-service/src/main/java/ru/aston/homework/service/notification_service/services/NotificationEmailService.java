package ru.aston.homework.service.notification_service.services;

import org.springframework.mail.javamail.JavaMailSender;

public interface NotificationEmailService {
    void sendEmail(String to, String subject, String message);
}
