package ru.aston.homework.service.notification_service.handlers;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.core.dto.EventType;
import ru.aston.core.dto.UserEventDTO;
import ru.aston.homework.service.notification_service.services.NotificationEmailServiceImpl;

@Component
@KafkaListener(topics = "users-events-topic")
public class UserEventHandler {
    private final NotificationEmailServiceImpl service;

    @Autowired
    public UserEventHandler(NotificationEmailServiceImpl service) {
        this.service = service;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handler(UserEventDTO userEventDTO) {
        String to = userEventDTO.getEmail();
        String subject = userEventDTO.getEventType() == EventType.CREATED?
                "Аккаунт создан"
                :"Аккаунт удалён";
        String message = userEventDTO.getEventType() == EventType.CREATED?
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан!"
                :"Здравствуйте! Ваш аккаунт был удалён...";
        service.sendEmail(to, subject, message);
        logger.info("Received: {} {}", userEventDTO.getEmail(), userEventDTO.getEventType());
    }
}
