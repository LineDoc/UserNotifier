package ru.aston.CRUDSpringApp.models;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс {@link User} представляет собой некоторое описание пользователей.
 * Аннотация @Entity (сущность) позволяет представить класс в виде таблицы (ORM), в которой каждый объект
 * данного класса является записью (строкой). Поля класса при этом олицетворяют столбцы таблицы.
 * По-умолчанию имя таблицы совпадает с именем сущности.
 * Поскольку имя {@link User} уже зарезервировано в PostgreSQL как служебное, используется аннотация @Table с
 * параметром name = "Person", которая позволяет явно связать нашу сущность {@link User} и таблицу Person в БД.
 */
@Entity
@Table(name = "People")
public class User {
    /**
     * Поле id представляет собой уникальный идентификатор пользователя.
     * Генерируется автоматически на стороне БД
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Поле name - имя конкретного пользователя, не уникальное.
     */
    @Column(name = "name")
    private String name;
    /**
     * Поле email - адрес электронной почты пользователя. Должно быть уникальным.
     */
    @Column(name = "email")
    private String email;
    /**
     * Поле age - возраст конкретного пользователя.
     */
    @Column(name = "age")
    private int age;
    /**
     * Поле createAt - время создания конкретного экземпляра User
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    /**
     * Поле updatedAt - время обновления конкретного экземпляра User
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public User(String name, String email, int age, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(int id, String name, String email, int age, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User)) return false;
        User user = (User) object;
        return id == user.id && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "id: " + id
                + ", name: " + name
                + ", age: " + age
                + ", email: " + email
                + ", createdAt: " + createdAt
                + ", updatedAt: " + updatedAt;
    }

}
