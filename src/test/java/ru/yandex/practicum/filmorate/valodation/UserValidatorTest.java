package ru.yandex.practicum.filmorate.valodation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

public class UserValidatorTest {

    @Test
    void validateIsValidUser() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void validateUserWithBlankEmail() {
        UserService service = new UserService();
        User user = new User(1, " ", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithNullEmail() {
        UserService service = new UserService();
        User user = new User(1, null, "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyEmail() {
        UserService service = new UserService();
        User user = new User(1, "", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithNotValidEmail() {
        UserService service = new UserService();
        User user = new User(1, "mail", "dolore", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithBlankLogin() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", " ", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyLogin() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", "", "Nick Name", LocalDate.of(1946, 1, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithWrongBirthday() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(2100, 6, 20));
        Assertions.assertEquals(0, service.getAllUsers().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addUser(user));
    }

    @Test
    void validateUserWithEmptyName() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", "dolore", "", LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void validateUserWithNullName() {
        UserService service = new UserService();
        User user = new User(1, "mail@mail.ru", "dolore", null, LocalDate.of(1946, 1, 20));
        service.addUser(user);
        Assertions.assertEquals(1, service.getAllUsers().size());
    }
}
