package ru.aston.homework.service.notification_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.homework.service.notification_service.dto.EmailRequest;
import ru.aston.homework.service.notification_service.services.NotificationEmailServiceImpl;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationEmailServiceImpl notificationEmailServiceImpl;

    public NotificationController(NotificationEmailServiceImpl notificationEmailServiceImpl) {
        this.notificationEmailServiceImpl = notificationEmailServiceImpl;
    }
    @PostMapping("/send")
    public ResponseEntity<?> send (@RequestBody EmailRequest request) {
        notificationEmailServiceImpl.sendEmail(request.getEmail(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok().build();
    }
}
