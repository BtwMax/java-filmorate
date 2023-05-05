package ru.yandex.practicum.filmorate.valodation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.inmemory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidatorTest {

    @Test
    void validateIsValidUser() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void validateUserWithBlankEmail() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, " ", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithNullEmail() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, null, "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyEmail() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithNotValidEmail() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithBlankLogin() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", " ", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyLogin() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", "", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithWrongBirthday() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(2100, 6, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyName() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", "dolore", "", LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void validateUserWithNullName() {
        InMemoryUserStorage service = new InMemoryUserStorage();
        User user = new User(1, "mail@mail.ru", "dolore", null, LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }
}
