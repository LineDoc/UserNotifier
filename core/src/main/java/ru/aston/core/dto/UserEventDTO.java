package ru.aston.core.dto;

/**
 * {@link UserEventDTO} представляет собой событие, формируемое при создании или удалении аккаунта.
 * Содержит в себе email пользователя и тип {@link EventType} события
 */
public class UserEventDTO {
    private String email;
    private EventType eventType;

    public UserEventDTO(String email, EventType eventType) {
        this.email = email;
        this.eventType = eventType;
    }

    public UserEventDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
