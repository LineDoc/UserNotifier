package ru.aston.CRUDSpringApp.services;

import ru.aston.CRUDSpringApp.models.User;

import java.util.List;

public interface UsersService {
    List<User> findAllUsers();
    User findUserById(int id);
    User findUserByEmail(String email);
    User save(User user);
    User updateById(int id, User updatedUser);
    User updateByEmail(String email, User updatedUser);
    void deleteById(int id);
    void deleteByEmail(String email);
    void deleteAll();
}
