package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.inmemory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {
    InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUser(User user) {
        userStorage.addUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void updateUser(User user) {
        userStorage.updateUser(user);
    }

    public void removeUser(long id) {
        userStorage.removeUser(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new NotFoundException("Нельзя добавить самого себя");
        }
        /*Проверка, существуют ли пользователи*/
        userStorage.userExistsById(id);
        userStorage.userExistsById(friendId);

        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new NotFoundException("Нельзя удалить самого себя");
        }
        /*Проверка, существуют ли пользователи*/
        userStorage.userExistsById(id);
        userStorage.userExistsById(friendId);

        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getAllUserFriends(long id) {
        User user = userStorage.getUserById(id);

        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long firstUserId, long secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);

        return firstUser.getFriends().stream()
                .filter(secondUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
