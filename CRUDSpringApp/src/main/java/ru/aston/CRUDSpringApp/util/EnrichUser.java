package ru.aston.CRUDSpringApp.util;

import ru.aston.CRUDSpringApp.models.User;

import java.time.LocalDateTime;

public class EnrichUser {
    /**
     * Метод осуществляет установку времени создания и обновления объекта
     */
    public static void enrich(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }
}
