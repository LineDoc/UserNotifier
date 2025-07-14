package ru.aston.CRUDSpringApp.integration.services;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import ru.aston.CRUDSpringApp.dto.UserDTO;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.services.UsersService;
import ru.aston.CRUDSpringApp.util.Converter;
import ru.aston.core.dto.EventType;
import ru.aston.core.dto.UserEventDTO;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционный тест для проверки взаимодействия с Kafka.
 * Тестирует отправку событий в Kafka при создании и удалении пользователей.
 * Использует Embedded Kafka для имитации реального Kafka-брокера в тестовой среде.
 *
 * @see EmbeddedKafka
 * @see UsersService
 * @see UserEventDTO
 */
@DirtiesContext
@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class UserServiceKafkaIntegrationTest {
    //Название темы Kafka для событий пользователей
    private static final String TOPIC_NAME = "users-events-topic";
    //Идентификатор группы потребителя
    private static final String GROUP_ID = "test-group";
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private UsersService usersService;
    @Autowired
    Converter converter;
    private Consumer<String, UserEventDTO> consumer;

    /**
     * Настройка тестового окружения перед каждым тестом.
     * Создает и настраивает потребителя для чтения сообщений из тестовой темы.
     * Конфигурирует десериализаторы для ключа (String) и значения (UserEvent).
     */
    @BeforeEach
    public void setup() {
        // Настройка свойств потребителя
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_ID, "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.aston.core");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserEventDTO.class);

        // Создание фабрики потребителей
        DefaultKafkaConsumerFactory<String, UserEventDTO> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        // Создание и подписка потребителя
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(TOPIC_NAME));
    }

    /**
     * Очистка тестового окружения после каждого теста.
     * Закрывает потребителя, если он был создан.
     */
    @AfterEach
    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    /**
     * Тестирует отправку события в Kafka при создании пользователя.
     * Проверяет, что при создании пользователя через UsersService:
     * 1. Отправляется ровно одно сообщение в Kafka
     * 2. Ключ сообщения соответствует email пользователя
     * 3. Тип события равен CREATED
     * 4. Email в теле события соответствует email пользователя
     */
    @Test
    public void testCreateUserSendsKafkaEvent() {
        String email = "test@mail.com";

        UserDTO userDTO = new UserDTO("Test User", email, 25);
        User createdUser = usersService.save(converter.convertToUser(userDTO));

        ConsumerRecords<String, UserEventDTO> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

        assertEquals(1, records.count(), "Должно быть получено ровно одно сообщение");
        ConsumerRecord<String, UserEventDTO> record = records.iterator().next();
        assertNotNull(record, "Сообщение не должно быть null");
        assertEquals(email, record.key(), "Ключ сообщения должен соответствовать email пользователя");
        assertEquals(EventType.CREATED, record.value().getEventType(), "Тип события должен быть CREATED");
        assertEquals(email, record.value().getEmail(), "Email в событии должен соответствовать email пользователя");

        usersService.deleteById(createdUser.getId());
    }

    /**
     * Тестирует отправку события в Kafka при удалении пользователя.
     * Проверяет, что при удалении пользователя через UsersService:
     * 1. Отправляется ровно одно сообщение в Kafka
     * 2. Ключ сообщения соответствует email пользователя
     * 3. Тип события равен DELETED
     * 4. Email в теле события соответствует email пользователя
     */
    @Test
    public void testDeleteUserSendsKafkaEvent() {
        String email = "delete-test@example.com";

        UserDTO userDTO = new UserDTO("Delete Test", email, 30);
        User userToDelete = usersService.save(converter.convertToUser(userDTO));

        KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(1));

        usersService.deleteById(userToDelete.getId());

        ConsumerRecords<String, UserEventDTO> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

        assertEquals(1, records.count(), "Должно быть получено ровно одно сообщение");
        ConsumerRecord<String, UserEventDTO> record = records.iterator().next();
        assertNotNull(record, "Сообщение не должно быть null");
        assertEquals(email, record.key(), "Ключ сообщения должен соответствовать email пользователя");
        assertEquals(EventType.DELETED, record.value().getEventType(), "Тип события должен быть DELETED");
        assertEquals(email, record.value().getEmail(), "Email в событии должен соответствовать email пользователя");
    }
}