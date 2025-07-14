package ru.aston.homework.service.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/**
 * Реализация сервиса для отправки email-уведомлений.
 * Использует {@link JavaMailSender} для отправки простых текстовых email-сообщений.
 */
@Service
public class NotificationEmailServiceImpl implements NotificationEmailService{
    private final JavaMailSender javaMailSender;
    /**
     * Адрес отправителя
     */
    private final String from;

    @Autowired
    public NotificationEmailServiceImpl(JavaMailSender javaMailSender,
                                        @Value("${spring.mail.username}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    /**
     * Метод отправки сообщения на почту.
     * @param to - кому предназначено письмо, адрес
     * @param subject - заголовок
     * @param message - сообщение
     *
     */
    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }
}
