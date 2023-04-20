package ru.yandex.practicum.filmorate.conrtollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
class UserController {
    UserService userService = new UserService();

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя");
        userService.addUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя");
        userService.updateUser(user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }
}
