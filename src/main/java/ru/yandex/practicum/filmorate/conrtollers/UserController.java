package ru.yandex.practicum.filmorate.conrtollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя");
        userService.addUser(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") long id) {
        log.info("Запрос на показ пользователя в id = " + id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        log.info("Запрос на удаление пользователя с id = " + id);
        userService.removeUser(id);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя");
        userService.updateUser(user);
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        log.info("Запрос пользователя " + id + " на добавление в друзья пользователя " + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        log.info("Запрос пользователя " + id + " на удаление пользователя " + friendId + " из друзей");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") long id) {
        log.info("Запрос на показ друзей пользователя " + id);
        return userService.getAllUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long firstUserId,
                                       @PathVariable("otherId") long secondUserId) {
        log.info("Запрос на показ списка общих друзей у пользователей " + firstUserId + " и " + secondUserId);
        return userService.getCommonFriends(firstUserId, secondUserId);
    }
}
