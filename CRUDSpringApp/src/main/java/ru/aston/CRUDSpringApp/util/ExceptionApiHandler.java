package ru.aston.CRUDSpringApp.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.aston.CRUDSpringApp.exceptions.UserNotCreatedException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByEmailException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByIdException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionApiHandler {
    /**
     * Методы {@link #handlerException(UserNotFoundByIdException)},
     * {@link #handlerException(UserNotFoundByEmailException)} и {@link #handleException(UserNotCreatedException)}
     * осуществляют перехват исключений и их отображение в удобном для чтения виде
     */
    @ExceptionHandler(UserNotFoundByIdException.class)
    private ResponseEntity<UserErrorResponse> handlerException(UserNotFoundByIdException e) {
        UserErrorResponse response = new UserErrorResponse("Пользователь с таким ID не найден!", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundByEmailException.class)
    private ResponseEntity<UserErrorResponse> handlerException(UserNotFoundByEmailException e) {
        UserErrorResponse response = new UserErrorResponse("Пользователь с таким Email не найден!", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotCreatedException.class)
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
