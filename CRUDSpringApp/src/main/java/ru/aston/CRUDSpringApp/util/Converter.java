package ru.aston.CRUDSpringApp.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.aston.CRUDSpringApp.dto.UserDTO;
import ru.aston.CRUDSpringApp.models.User;
@Component
public class Converter {
    private final ModelMapper modelMapper;

    public Converter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Метод {@link #convertToUser(UserDTO)} конвертирует полученную в качестве параметра сущность типа {@code UserDTO}
     * в {@code User} (производит сопоставление полей). Во избежание ручной конвертации и уменьшения
     * количества кода используется класс {@link ModelMapper}, который вносит все необходимые изменения
     */
    public User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    /**
     * Метод {@link #convertToUserDTO(User)} конвертирует полученную в качестве параметра сущность типа {@code User}
     * в {@code UserDTO} (производит сопоставление полей). Во избежание ручной конвертации и уменьшения
     * количества кода используется класс {@link ModelMapper}, который вносит все необходимые изменения
     */
    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
