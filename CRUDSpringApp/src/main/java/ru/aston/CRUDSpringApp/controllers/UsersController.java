package ru.aston.CRUDSpringApp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.CRUDSpringApp.dto.UserDTO;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.services.UsersService;
import ru.aston.CRUDSpringApp.exceptions.UserNotCreatedException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByIdException;
import ru.aston.CRUDSpringApp.util.Converter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер {@link UsersController} обрабатывает поступающие запросы и направляет их в соответствующие методы.
 * Поскольку класс {@link UsersController} помечен аннотацией @RestController возвращаемые данные имеют формат JSON
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;
    private final Converter converter;

    @Autowired
    public UsersController(UsersService usersService, Converter converter) {
        this.usersService = usersService;
        this.converter = converter;
    }

    /**
     * Метод {@link #getUsers()} возвращает всех пользователей в формате
     */
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> result = usersService.findAllUsers();
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersService.findAllUsers()
                .stream()
                .map(converter::convertToUserDTO)
                .collect(Collectors.toList()));
    }

    /**
     * Метод {@link #getUserById(int id)} возвращает конкретного пользователя по его {@code id}
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.ok(converter.convertToUserDTO(usersService.findUserById(id)));
    }

    /**
     * Метод {@link #getUserByEmail(String email)} возвращает конкретного пользователя по его {@code email}
     * (считаем, что {@code email} является уникальным)
     */
    @GetMapping("/email/{email}")
    public UserDTO getUserByEmail(@PathVariable("email") String email) {
        return converter.convertToUserDTO(usersService.findUserByEmail(email));
    }

    /**
     * Метод {@link #createUser(UserDTO, BindingResult)} создаёт нового пользователя на основе
     * поступающего в формате JSON {@link UserDTO userDTO} (при отсутствии ошибок в поступающих данных).
     * В случае получения данных с ошибками (например, неправильный формат Email или отрицательный возраст)
     * будет выведено сообщение об ошибке.
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UserNotCreatedException(errorMessage.toString());
        }
        return new ResponseEntity<>(usersService.save(converter.convertToUser(userDTO)), HttpStatus.CREATED);
    }

    /**
     * Методы {@link #updateUser(int, UserDTO, BindingResult)} и {@link #updateUser(String, UserDTO, BindingResult)}
     * позволяют обновить данные пользователя. Предварительно осуществляется поиск обновляемой сущности
     * по её {@code id} или {@code email}
     */
    @PutMapping("/update/byId/{id}")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable int id, @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        usersService.updateById(id, converter.convertToUser(userDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/update/byEmail/{email}")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable String email, @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        usersService.updateByEmail(email, converter.convertToUser(userDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Метод {@link #deleteUser(int)} позволяет удалить пользователя по заданному {@code id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int id) {
        try {
            usersService.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (UserNotFoundByIdException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Метод {@link #deleteAll()} производит полную очистку таблицы от всех записей
     */
    @DeleteMapping("/clear")
    public ResponseEntity<HttpStatus> deleteAll() {
        try {
            usersService.deleteAll();
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (UserNotFoundByIdException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
