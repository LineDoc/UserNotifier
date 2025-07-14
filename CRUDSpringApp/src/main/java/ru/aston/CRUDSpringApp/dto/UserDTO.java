package ru.aston.CRUDSpringApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTO {
    /**
     * Поле name - имя конкретного пользователя, не уникальное.
     */
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов")
    private String name;
    /**
     * Поле email - адрес электронной почты пользователя. Должно быть уникальным.
     */
    @Email
    @NotEmpty(message = "Email не может быть пустым")
    private String email;
    /**
     * Поле age - возраст конкретного пользователя.
     */
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    private int age;

    public UserDTO(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public UserDTO() {
    }

    public @NotEmpty(message = "Имя не может быть пустым") @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "Имя не может быть пустым") @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов") String name) {
        this.name = name;
    }

    public @Email @NotEmpty(message = "Email не может быть пустым") String getEmail() {
        return email;
    }

    public void setEmail(@Email @NotEmpty(message = "Email не может быть пустым") String email) {
        this.email = email;
    }

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    public int getAge() {
        return age;
    }

    public void setAge(@Min(value = 0, message = "Возраст не может быть отрицательным") int age) {
        this.age = age;
    }
}
