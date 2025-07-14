package ru.aston.CRUDSpringApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.aston.CRUDSpringApp.models.User;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    /**
     * {@link #findByEmail(String)} и {@link #deleteByEmail(String)} - кастомные методы для поиска и, соответственно,
     * удаление записей по {@code email}
     */
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
}
