package ru.aston.homework.service.notification_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.homework.service.notification_service.dto.EmailRequestDTO;
import ru.aston.homework.service.notification_service.services.NotificationEmailServiceImpl;

/**
 * Обработка входящих запросов
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationEmailServiceImpl notificationEmailServiceImpl;

    @Autowired
    public NotificationController(NotificationEmailServiceImpl notificationEmailServiceImpl) {
        this.notificationEmailServiceImpl = notificationEmailServiceImpl;
    }

    /**
     * Отправка POST запроса для уведомления User-а об удалении или создании записи
     * @param request
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<?> send (@RequestBody EmailRequestDTO request) {
        notificationEmailServiceImpl.sendEmail(request.getEmail(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok().build();
    }
}
