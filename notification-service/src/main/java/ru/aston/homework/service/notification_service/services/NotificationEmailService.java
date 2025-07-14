package ru.aston.homework.service.notification_service.services;
/**
 * Сервис для отправки email-уведомлений.
 * Определяет контракт для реализации сервиса отправки электронной почты.
 */
public interface NotificationEmailService {
    /**
     * Метод отправки Email по сети.
     * @param to - кому предназначено письмо, адрес
     * @param subject - заголовок
     * @param message - сообщение
     */
    void sendEmail(String to, String subject, String message);
}
