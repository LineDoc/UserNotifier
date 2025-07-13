package ru.aston.CRUDSpringApp.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByEmailException;
import ru.aston.CRUDSpringApp.exceptions.UserNotFoundByIdException;
import ru.aston.CRUDSpringApp.models.User;
import ru.aston.CRUDSpringApp.repositories.UsersRepository;
import ru.aston.CRUDSpringApp.services.UsersServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersServiceUnitTestImpl {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceImpl usersServiceImpl;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setup() {
        testUser1 = new User(1, "testUser1", "testUser1@mail.com", 18, LocalDateTime.now(), LocalDateTime.now());
        testUser2 = new User(2, "testUser2", "testUser2@mail.com", 81, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void testFindAllUsersShouldReturnAllUsers() {
        when(usersRepository.findAll()).thenReturn(List.of(testUser1, testUser2));

        List<User> result = usersServiceImpl.findAllUsers();

        assertEquals(2, result.size());
        verify(usersRepository, times(1)).findAll();
    }

    @Test
    void testFindUserByIdWhenUserExistsShouldReturnUser() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(testUser1));

        User result = usersServiceImpl.findUserById(1);

        assertNotNull(result);
        assertEquals(testUser1.getId(), result.getId());
        verify(usersRepository, times(1)).findById(1);
    }

    @Test
    void testFindUserByIdWhenUserNotExistsShouldThrowUserNotFoundByIdException() {
        when(usersRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByIdException.class, () -> usersServiceImpl.findUserById(3));
        verify(usersRepository, times(1)).findById(3);
    }

    @Test
    void testFindUserByEmailWhenUserExistsShouldReturnUser() {
        when(usersRepository.findByEmail("testUser2@mail.com")).thenReturn(Optional.of(testUser2));

        User result = usersServiceImpl.findUserByEmail("testUser2@mail.com");

        assertNotNull(result);
        assertEquals(testUser2.getEmail(), result.getEmail());
        verify(usersRepository, times(1)).findByEmail("testUser2@mail.com");
    }

    @Test
    void testFindUserByEmailWhenUserNotExistsShouldThrowUserNotFoundException() {
        when(usersRepository.findByEmail("testUser3@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByEmailException.class, () -> usersServiceImpl.findUserByEmail("testUser3@mail.com"));
    }

    @Test
    void testUpdateByIdWhenUserExistsShouldUpdateUser() {
        User updatedUserData = new User("newTestName", 12, "newTestEmail@mail.com");
        when(usersRepository.findById(1)).thenReturn(Optional.of(testUser1));
        when(usersRepository.save(any(User.class))).thenReturn(testUser1);

        User result = usersServiceImpl.updateById(1, updatedUserData);

        assertEquals("newTestName", result.getName());
        assertEquals("newTestEmail@mail.com", result.getEmail());
        assertEquals(12, result.getAge());
        assertNotNull(result.getUpdatedAt());
        verify(usersRepository, times(1)).findById(1);
        verify(usersRepository, times(1)).save(testUser1);
    }

    @Test
    void testUpdateByIdWhenUserNotExistsShouldThrowUserNotFoundByIdException() {
        User updatedData = new User();
        when(usersRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByIdException.class,
                () -> usersServiceImpl.updateById(10, updatedData));
    }

    @Test
    void testUpdateByEmailWhenUserExistsShouldUpdateUser() {
        User updatedData = new User();
        updatedData.setName("UpdatedName");
        updatedData.setAge(30);

        when(usersRepository.findByEmail("testUser1@mail.com")).thenReturn(Optional.of(testUser1));
        when(usersRepository.save(any(User.class))).thenReturn(testUser1);

        User result = usersServiceImpl.updateByEmail("testUser1@mail.com", updatedData);

        assertEquals("UpdatedName", result.getName());
        assertEquals(30, result.getAge());
        assertNotNull(result.getUpdatedAt());
        verify(usersRepository, times(1)).findByEmail("testUser1@mail.com");
        verify(usersRepository, times(1)).save(testUser1);
    }

    @Test
    void testUpdateByEmailWhenUserNotExistsShouldThrowException() {
        User updatedUserData = new User();
        when(usersRepository.findByEmail("testUser3@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByEmailException.class,
                () -> usersServiceImpl.updateByEmail("testUser3@mail.com", updatedUserData));
    }

    @Test
    void testDeleteByIdShouldCallRepository() {
        doNothing().when(usersRepository).deleteById(1);

        usersServiceImpl.deleteById(1);

        verify(usersRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteByEmailShouldCallRepository() {
        doNothing().when(usersRepository).deleteByEmail("testUser2@mail.com");

        usersServiceImpl.deleteByEmail("testUser2@mail.com");

        verify(usersRepository, times(1)).deleteByEmail("testUser2@mail.com");
    }

    @Test
    void testDeleteAllShouldCallRepository() {
        doNothing().when(usersRepository).deleteAll();

        usersServiceImpl.deleteAll();

        verify(usersRepository, times(1)).deleteAll();
    }
}
