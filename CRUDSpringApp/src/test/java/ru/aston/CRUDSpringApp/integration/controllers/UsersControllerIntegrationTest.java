package ru.aston.CRUDSpringApp.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.aston.CRUDSpringApp.dto.UserDTO;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.repositories.UsersRepository;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerIntegrationTest {
    @LocalServerPort
    private Integer port;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Container
    static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeAll
    public static void startContainer() {
        container.start();
    }

    @AfterAll
    public static void stopContainer() {
        container.stop();
    }

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port + "/users";
        usersRepository.deleteAll();
    }

    /**
     * Метод {@link #testGetUsersShouldReturnAllUsersDTO200Ok()} осуществляет тестирование получения всех пользователей из БД
     */
    @Test
    void testGetUsersShouldReturnAllUsersDTO200Ok() {
        /**
         * Создаём список пользователей и сохраняем в репозиторий
         */
        List<User> users = List.of(new User("testUser1",23, "testUser1@mail.com"),
                new User("testUser2",32, "testUser2@mail.com"));

        usersRepository.saveAll(users);
        /**
         * Осуществляем запрос. Должны получить код 200 Ок и всех пользователей в теле ответа в формате JSON
         */
        given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .log().status()
                .log().body();
    }

    /**
     * Метод {@link #testGetUsersWhenDbIsEmptyShouldReturn204NoContent()} осуществляет тестирование получения всех пользователей,
     * при условии, что записи в БД отсутствуют.
     * Должны получить код 204 No Content и пустое тело ответа.
     */
    @Test
    void testGetUsersWhenDbIsEmptyShouldReturn204NoContent() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(204)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testGetUserByIdWhenUserExistShouldReturnUserDTO200Ok()} проверяет поиск и получение пользователя по заданному id.
     * Должны получить код 200 Ок и данные искомого пользователя в формате JSON
     */
    @Test
    void testGetUserByIdWhenUserExistShouldReturnUserDTO200Ok() {
        User testUser = new User("testUser", 23, "testUser@mail.com");

        usersRepository.save(testUser);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/id/" + testUser.getId())
                .then()
                .statusCode(200)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testGetUserByIdWhenUserNoExistShouldReturn404NotFound()} проверка получения пользователя
     * по несуществующему в БД id.
     * Должны получить код ответа 404 Not Found.
     */
    @Test
    void testGetUserByIdWhenUserNoExistShouldReturn404NotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/id/1")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testGetUserByEmailShouldReturn200Ok} - тест поиска пользователя по mail.
     * Должны получить код ответа 200 Ок и данные искомого пользователя в формате JSON.
     */
    @Test
    void testGetUserByEmailShouldReturn200Ok() {
        User testUser = new User("testUser", 23, "testUser@mail.com");

        usersRepository.save(testUser);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/email/" + testUser.getEmail())
                .then()
                .statusCode(200).log().body();
    }
    /**
     * {@link #testGetUserByEmailWhenUserNoExistShouldReturn404NotFound()} - тест поиска пользователя по несуществующему mail.
     * Должны получить код ответа 404 Not Found и пустое тело ответа.
     */
    @Test
    void testGetUserByEmailWhenUserNoExistShouldReturn404NotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/email/noexist@mail.com")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testPostCreateUserShouldReturn201Created()} - тестирование создания новой записи в БД.
     * Должны получить код ответа 201 Created и данные пользователя в формате JSON
     */
    @Test
    void testPostCreateUserShouldReturn201Created() {
        UserDTO newTestUserDTO = new UserDTO();
        newTestUserDTO.setName("testName");
        newTestUserDTO.setAge(44);
        newTestUserDTO.setEmail("test@mail.com");

        given()
                .contentType(ContentType.JSON)
                .body(newTestUserDTO)
                .when()
                .post("/create")
                .then()
                .statusCode(201)
                .log().status()
                .log().body();
    }

    /**
     *{@link #testPostCreateUserWithInvalidDataShouldReturn400BadRequest()} - тестирование создания нового пользователя
     * при некорректных данных
     * Должны получить код ответа 400 и сообщение об ошибке
     * */
    @Test
    void testPostCreateUserWithInvalidDataShouldReturn400BadRequest() {
        UserDTO newTestUserDTO = new UserDTO();
        newTestUserDTO.setName("testName");
        newTestUserDTO.setAge(44);
        newTestUserDTO.setEmail("testmail.com");

        given()
                .contentType(ContentType.JSON)
                .body(newTestUserDTO)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testPutUpdateUserByEmailShouldReturn200Ok()} - тестирование обновления пользователя по email
     */
    @Test
    void testPutUpdateUserByIdShouldReturn200Ok() {
        UserDTO oldTestUserDTO = new UserDTO();
        oldTestUserDTO.setName("testName");
        oldTestUserDTO.setAge(44);
        oldTestUserDTO.setEmail("test@mail.com");
        UserDTO newTestUserDTO = new UserDTO();
        newTestUserDTO.setName("newTestName");
        newTestUserDTO.setAge(55);
        newTestUserDTO.setEmail("newTestEmail@mail.com");

        int id = given()
                .contentType(ContentType.JSON)
                .body(oldTestUserDTO)
                .when()
                .post("/create")
                .jsonPath().getInt("id");

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(newTestUserDTO)
                .put("/update/byId/{id}")
                .then()
                .statusCode(200)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testPutUpdateUserByEmailWhenUserNoExistShouldReturn404NotFound()} - тестирование обновления данных
     * пользователя по email при некорректных данных.
     * Должны получить код ответа 404 и сообщение об ошибке.
     */
    @Test
    void testPutUpdateUserByIdWhenUserNoExistShouldReturn404NotFound() {
        UserDTO newTestUserDTO = new UserDTO();
        newTestUserDTO.setName("newTestName");
        newTestUserDTO.setAge(55);
        newTestUserDTO.setEmail("newTestEmail@mail.com");

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 111)
                .body(newTestUserDTO)
                .put("/update/byId/{id}")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testPutUpdateUserByEmailShouldReturn200Ok()} - тестирование обновления пользователя по mail.
     */
    @Test
    void testPutUpdateUserByEmailShouldReturn200Ok() {
        UserDTO oldTestUserDTO = new UserDTO();
        oldTestUserDTO.setName("testName");
        oldTestUserDTO.setAge(44);
        oldTestUserDTO.setEmail("test@mail.com");
        UserDTO newTestUserDTO = new UserDTO();
        newTestUserDTO.setName("newTestName");
        newTestUserDTO.setAge(55);
        newTestUserDTO.setEmail("newTestEmail@mail.com");

        /**
         * Запись пользователя в БД и получение его mail.
         */
        String email = given()
                .contentType(ContentType.JSON)
                .body(oldTestUserDTO)
                .when()
                .post("/create")
                .jsonPath().getString("email");
        /**
         * Обновление только что созданного пользователя
         */
        given()
                .contentType(ContentType.JSON)
                .pathParam("email", email)
                .body(newTestUserDTO)
                .put("/update/byEmail/{email}")
                .then()
                .statusCode(200)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testPutUpdateUserByIdWhenUserNoExistShouldReturn404NotFound()} - тестирование обновления данных
     * пользователя по несуществующему email
     */
    @Test
    void testPutUpdateUserByEmailWhenUserNoExistShouldReturn404NotFound() {
        UserDTO newTestUserDTO = new UserDTO();

        given()
                .contentType(ContentType.JSON)
                .pathParam("email", "noExist@mail.com")
                .body(newTestUserDTO)
                .put("/update/byEmail/{email}")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    /**
     * {@link #testDeleteUserShouldReturn200Ok()} - тестирование удаление пользователя по заданному id
     */
    @Test
    void testDeleteUserShouldReturn200Ok() {
        UserDTO testUserDTO = new UserDTO();
        testUserDTO.setName("testName");
        testUserDTO.setAge(44);
        testUserDTO.setEmail("test@mail.com");
        /**
         * Создаём новую запись в БД, получаем id нового пользователя
         */
        int id = given()
                .contentType(ContentType.JSON)
                .body(testUserDTO)
                .when()
                .post("/create")
                .jsonPath().getInt("id");
        /**
         * Удаление только что созданного пользователя
         */
        given()
                .pathParam("id", id)
                .when()
                .delete("/delete/{id}")
                .then()
                .statusCode(200)
                .log().status();
        /**
         * Проверка отсутствия записи после удаления
         */
        given()
                .pathParam("id", id)
                .when()
                .get("/id/{id}")
                .then()
                .statusCode(404)
                .log().status();
    }

    /**
     * {@link #testDeleteAllUserShouldReturn200Ok()} - тестирование удаления всех записей из БД.
     */
    @Test
    void testDeleteAllUserShouldReturn200Ok() {
        List<User> users = List.of(new User("testUser1",23, "testUser1@mail.com"),
                new User("testUser2",32, "testUser2@mail.com"));
        /**
         * Запись списка пользователей
         */
        usersRepository.saveAll(users);
        /**
         * Удаление всех пользователей из списка
         */
        given()
                .when()
                .delete("/clear")
                .then()
                .statusCode(200)
                .log().status();
        /**
         * Проверка наличия записей после удаления
         */
        given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(204)
                .log().status();
    }

}
