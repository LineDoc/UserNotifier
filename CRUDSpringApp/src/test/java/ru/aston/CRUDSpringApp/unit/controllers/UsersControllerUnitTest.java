package ru.aston.CRUDSpringApp.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.CRUDSpringApp.controllers.UsersController;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.services.UsersServiceImpl;
import ru.aston.CRUDSpringApp.exceptions.UserNotCreatedException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByEmailException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByIdException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org. springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org. springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Модульные тесты для {@link UsersController} с применением MockMvc
 */
@WebMvcTest(UsersController.class)
public class UsersControllerUnitTest {
    private static final String PATH = "/users";
    @Autowired
    MockMvc mockMvc;
    /**
     * {@link ObjectMapper} позволяет преобразовывать наши объекты в формат JSON и наоборот.
     */
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UsersServiceImpl usersServiceImpl;

    private User testUser1;
    private User testUser2;
    private List<User> testUserList;

    /**
     * Метод {@link #setup()} вызывается перед каждым тестом для создания объектов {@link User} и добавления их в {@link List}
     */
    @BeforeEach
    public void setup() {
        testUser1 = new User(1, "test1", "test1@mail.com", 21, LocalDateTime.now(), LocalDateTime.now());
        testUser2 = new User(2, "test2", "test2@mail.com", 22, LocalDateTime.now(), LocalDateTime.now());
        testUserList = List.of(testUser1, testUser2);
    }

    /**
     * Метод {@link #testGetAllUsersShouldReturn200Ok()} тестирует получение списка пользователей (в формате JSON).
     * Возвращает код ответа 200 Ок
     * @throws Exception
     */
    @Test
    public void testGetAllUsersShouldReturn200Ok() throws Exception {
        Mockito.when(usersServiceImpl.findAllUsers()).thenReturn(testUserList);

        mockMvc.perform(get(PATH)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
    }
    /**
     * Метод {@link #testGetAllUsersShouldReturn204NoContent()} тестирует получение списка
     * пользователей (в формате JSON) в случае, если в БД отсутствуют записи.
     * Возвращает код ответа 204 No Content
     * @throws Exception
     */
    @Test
    public void testGetAllUsersShouldReturn204NoContent() throws Exception {
        Mockito.when(usersServiceImpl.findAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
    /**
     * Метод {@link #testGetUserByIdShouldReturn200Ok()} тестирует получение конкретного
     * пользователя по id (в формате JSON).
     * Возвращает код ответа 200 Ок
     * @throws Exception
     */
    @Test
    public void testGetUserByIdShouldReturn200Ok() throws Exception {
        String requestURI = PATH + "/id/" + testUser1.getId();

        Mockito.when(usersServiceImpl.findUserById(testUser1.getId())).thenReturn(testUser1);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
    }
    /**
     * Метод {@link #testGetUserByIdShouldReturn404NotFound()} тестирует получение конкретного
     * пользователя по id (в формате JSON) в случае отсутствия пользователя с заданным id.
     * Возвращает код ответа 404 Not Found.
     * @throws Exception
     */
    @Test
    public void testGetUserByIdShouldReturn404NotFound() throws Exception {
        String requestURI = PATH + "/id/" + testUser1.getId();

        Mockito.when(usersServiceImpl.findUserById(testUser1.getId())).thenThrow(UserNotFoundByIdException.class);

        mockMvc.perform(get(requestURI)).andExpect(status().isNotFound()).andDo(print());
    }
    /**
     * Метод {@link #testGetUserByEmailShouldReturn200Ok()} тестирует получение конкретного
     * пользователя по email (в формате JSON).
     * Возвращает код ответа 200 Ок
     * @throws Exception
     */
    @Test
    public void testGetUserByEmailShouldReturn200Ok() throws Exception {
        String requestURI = PATH + "/email/" + testUser1.getEmail();

        Mockito.when(usersServiceImpl.findUserByEmail(testUser1.getEmail())).thenReturn(testUser1);
        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
    }
    /**
     * Метод {@link #testGetUserByEmailShouldReturn404NotFound()} тестирует получение конкретного
     * пользователя по email (в формате JSON) в случае отсутствия пользователя с таким email.
     * Возвращает код ответа 404 Not Found.
     * @throws Exception
     */
    @Test
    public void testGetUserByEmailShouldReturn404NotFound() throws Exception {
        String requestURI = PATH + "/email/" + testUser1.getEmail();

        Mockito.when(usersServiceImpl.findUserByEmail(testUser1.getEmail())).thenThrow(UserNotFoundByEmailException.class);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    /**
     * Метод {@link #testPostCreateUserShouldReturn201Created()} тестирует создание и сохранение нового
     * пользователя.
     * Возвращает код ответа 201 Created.
     * @throws Exception
     */
    @Test
    public void testPostCreateUserShouldReturn201Created() throws Exception {
        String requestURI = PATH + "/create";
        String requestBody = objectMapper.writeValueAsString(testUser2);

        mockMvc.perform(post(requestURI).contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }
    /**
     * Метод {@link #testPostCreateUserShouldReturn400BadRequest()} тестирует создание и сохранение нового
     * пользователя в случае ошибок входных данных.
     * Возвращает код ответа 400 Bad Request.
     * @throws Exception
     */
    @Test
    public void testPostCreateUserShouldReturn400BadRequest() throws Exception {
        String requestURI = PATH + "/create";
        testUser1.setName("");
        String requestBody = objectMapper.writeValueAsString(testUser1);

        mockMvc.perform(post(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserByIdShouldReturn200Ok()} тестирует обновление данных существующего
     * пользователя.
     * Должен вернуться код ответа 200 Ок.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserByIdShouldReturn200Ok() throws Exception{
        String requestURI = PATH + "/update/byId/" + testUser2.getId();
        String updatedUser = objectMapper.writeValueAsString(testUser2);

        Mockito.when(usersServiceImpl.updateById(testUser2.getId(), testUser1)).thenReturn(testUser2);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updatedUser))
                .andExpect(status().isOk())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserByIdShouldReturn404NotFound()} тестирует обновление данных
     * пользователя в случае, если пользователь с заданным id не найден.
     * Поиск пользователя осуществляется по id.
     * Должен вернуться код ответа 404 Not Found.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserByIdShouldReturn404NotFound() throws Exception{
        String requestURI = PATH + "/update/byId/" + testUser1.getId();
        User updateUser = new User("updatedUser", 33, "updatedUser@mail.com");
        String updateUserJson = objectMapper.writeValueAsString(updateUser);

        Mockito.when(usersServiceImpl.updateById(testUser1.getId(), updateUser)).thenThrow(UserNotFoundByIdException.class);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updateUserJson))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserShouldReturn500BadRequest()} тестирует обновление данных существующего
     * пользователя, если передана некорректная информация (name, email или age).
     * Поиск пользователя осуществляется по id.
     * Должен вернуться код ответа 500 Bad Request.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserShouldReturn500BadRequest() throws Exception {
        String requestURI = PATH + "/update/byId/" + testUser1.getId();
        User updateUser = new User("updatedUser", 33, "updatedUser@mail.com");
        String updateUserJson = objectMapper.writeValueAsString(updateUser);

        Mockito.when(usersServiceImpl.updateById(testUser1.getId(), updateUser)).thenThrow(UserNotCreatedException.class);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updateUserJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserByEmailShouldReturn200Ok()} тестирует обновление данных существующего
     * пользователя.
     * Поиск пользователя осуществляется по email.
     * Должен вернуться код ответа 200 Ок.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserByEmailShouldReturn200Ok() throws Exception{
        String requestURI = PATH + "/update/byEmail/" + testUser2.getEmail();
        String updatedUser = objectMapper.writeValueAsString(testUser2);

        Mockito.when(usersServiceImpl.updateByEmail(testUser2.getEmail(), testUser1)).thenReturn(testUser2);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updatedUser))
                .andExpect(status().isOk())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserByEmailShouldReturn404NotFound()} тестирует обновление данных существующего
     * пользователя, если пользователь с заданным id не найден.
     * Поиск пользователя осуществляется по email.
     * Должен вернуться код ответа 404 Bad Request.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserByEmailShouldReturn404NotFound() throws Exception{
        User updateUser = new User("updatedUser", 33, "updatedUser@mail.com");
        String updateUserJson = objectMapper.writeValueAsString(updateUser);
        String requestURI = PATH + "/update/byEmail/" + testUser1.getEmail();

        Mockito.when(usersServiceImpl.updateByEmail(testUser1.getEmail(), updateUser)).thenThrow(UserNotFoundByEmailException.class);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updateUserJson))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    /**
     * Метод {@link #testPutUpdateUserByEmailShouldReturn500BadRequest()} тестирует обновление данных существующего
     * пользователя, если передана некорректная информация (name, email или age).
     * Поиск пользователя осуществляется по email.
     * Должен вернуться код ответа 500 Bad Request.
     * @throws Exception
     */
    @Test
    public void testPutUpdateUserByEmailShouldReturn500BadRequest() throws Exception {
        User updateUser = new User("updatedUser", 33, "updatedUser@mail.com");
        String updateUserJson = objectMapper.writeValueAsString(updateUser);
        String requestURI = PATH + "/update/byEmail/" + testUser1.getEmail();

        Mockito.when(usersServiceImpl.updateByEmail(testUser1.getEmail(), updateUser)).thenThrow(UserNotCreatedException.class);

        mockMvc.perform(put(requestURI).contentType("application/json").content(updateUserJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    /**
     * Метод {@link #testDeleteShouldReturn200Ok()} тестирует удаление существующего
     * пользователя.
     * Поиск пользователя осуществляется по id.
     * Должен вернуться код ответа 200 Ок.
     * @throws Exception
     */
    @Test
    public void testDeleteShouldReturn200Ok() throws Exception {
        String requestURI = PATH + "/delete/" + testUser1.getId();

        doNothing().when(usersServiceImpl).deleteById(testUser1.getId());

        mockMvc.perform(delete(requestURI)).andExpect(status().isOk());
    }
    /**
     * Метод {@link #testDeleteShouldReturn404NotFound()} тестирует удаление существующего
     * пользователя, если пользователь с заданным id е найден.
     * Поиск пользователя осуществляется по id.
     * Должен вернуться код ответа 200 Ок.
     * @throws Exception
     */
    @Test
    public void testDeleteShouldReturn404NotFound() throws Exception {
        String requestURI = PATH + "/delete/" + testUser1.getId();

        doThrow(UserNotFoundByIdException.class).when(usersServiceImpl).deleteById(testUser1.getId());

        mockMvc.perform(delete(requestURI)).andExpect(status().isNotFound());
    }
    /**
     * Метод {@link #testDeleteAllShouldReturn200Ok()} тестирует удаление всех имеющихся
     * пользователей.
     * Должен вернуться код ответа 200 Ок.
     * @throws Exception
     */
    @Test
    public void testDeleteAllShouldReturn200Ok() throws Exception {
        String requestURI = PATH + "/clear";

        doNothing().when(usersServiceImpl).deleteAll();

        mockMvc.perform(delete(requestURI)).andExpect(status().isOk());
    }
}
