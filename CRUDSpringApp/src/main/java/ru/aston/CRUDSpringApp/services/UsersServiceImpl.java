package ru.aston.CRUDSpringApp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.repositories.UsersRepository;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByEmailException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByIdException;
import ru.aston.CRUDSpringApp.util.EnrichUser;
import ru.aston.CRUDSpringApp.util.UpdateUserData;
import ru.aston.core.dto.EventType;
import ru.aston.core.dto.UserEventDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class UsersServiceImpl implements UsersService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;
    private final UsersRepository usersRepository;

    @Autowired
    public UsersServiceImpl(KafkaTemplate<String, UserEventDTO> kafkaTemplate, UsersRepository usersRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.usersRepository = usersRepository;
    }
    /**
     * Поиск всех имеющихся сущностей User в БД
     */
    public List<User> findAllUsers() {
        return usersRepository.findAll();
    }
    /**
     * Поиск сущности User в БД по ID, оборачиваем в Optional для удобства.
     */
    public User findUserById(int id) {
        Optional<User> foundUser = usersRepository.findById(id);
        return foundUser.orElseThrow(UserNotFoundByIdException::new);
    }
    /**
     * Поиск сущности User в БД по email, оборачиваем в Optional для удобства.
     */
    public User findUserByEmail(String email) {
        Optional<User> foundUser = usersRepository.findByEmail(email);
        return foundUser.orElseThrow(UserNotFoundByEmailException::new);
    }

    /**
     * Метод осуществляет сохранение данных нового пользователя в репозиторий
     * @param user
     */
    @Transactional
    public User save(User user) {
        EnrichUser.enrich(user);
        User savedUser = usersRepository.save(user);

        UserEventDTO userEventDTO = new UserEventDTO(savedUser.getEmail(), EventType.CREATED);
        CompletableFuture<SendResult<String, UserEventDTO>> future = kafkaTemplate
                .send("users-events-topic",savedUser.getEmail(), userEventDTO);
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error("Failed to send message: {}", exception.getMessage());
            } else {
                logger.info("Message sent successfully: {}", result.getRecordMetadata());
            }
        });
        logger.info("Return: {}", savedUser.getEmail());
        logger.info("Return: {}", savedUser.getId());
        logger.info("Return: {}", savedUser.getName());
        return savedUser;
    }

    /**
     * Обновление имеющейся сущности в БД. В качестве аргументов поступают id обновляемого пользователя и
     * его новые данные
     */
    @Transactional
    public User updateById(int id, User updatedUser) {
        Optional<User> findUser = usersRepository.findById(id);
        if(findUser.isEmpty()) {
            throw new UserNotFoundByIdException();
        }
        UpdateUserData.updateData(findUser, updatedUser);
        return usersRepository.save(findUser.get());
    }
    /**
     * Обновление имеющейся сущности в БД. В качестве аргументов поступают email обновляемого пользователя и
     * его новые данные
     */
    @Transactional
    public User updateByEmail(String email, User updatedUser) {
        Optional<User> oldUser = usersRepository.findByEmail(email);
        if(oldUser.isEmpty()) {
            throw new UserNotFoundByEmailException();
        }
        UpdateUserData.updateData(oldUser, updatedUser);
        return usersRepository.save(oldUser.get());
    }

    /**
     * Поиск и удаление сущности в БД по ID
     */
    @Transactional
    public void deleteById(int id) {
        User deletedUser = findUserById(id);
        usersRepository.deleteById(id);

        UserEventDTO userEventDTO = new UserEventDTO(deletedUser.getEmail(), EventType.DELETED);
        CompletableFuture<SendResult<String, UserEventDTO>> future = kafkaTemplate
                .send("users-events-topic",deletedUser.getEmail(), userEventDTO);
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error("Failed to send message: {}", exception.getMessage());
            } else {
                logger.info("Message sent successfully: {}", result.getRecordMetadata());
            }
        });
        logger.info("Return: {}", deletedUser.getEmail());
    }
    /**
     * Поиск и удаление сущности в БД по email
     */
    @Transactional
    public void deleteByEmail(String email) {
        usersRepository.deleteByEmail(email);
    }
    /**
     * Удаление всех сущностей в БД
     */
    @Transactional
    public void deleteAll() {
        usersRepository.deleteAll();
    }
}
