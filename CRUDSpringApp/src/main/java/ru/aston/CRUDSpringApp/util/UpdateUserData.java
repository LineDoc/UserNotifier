package ru.aston.CRUDSpringApp.util;

import ru.aston.CRUDSpringApp.models.User;

import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateUserData {
    /**
     * Метод осуществляет обновление старых данных на новые
     */
    public static void updateData(Optional<User> oldUser, User updatedUser) {
        oldUser.get().setName(updatedUser.getName());
        oldUser.get().setAge(updatedUser.getAge());
        oldUser.get().setEmail(updatedUser.getEmail());
        oldUser.get().setUpdatedAt(LocalDateTime.now());
    }
}
