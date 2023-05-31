package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;


@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserDbStorage userDbStorage,
                       FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
        this.friendshipDao = friendshipDao;
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
        friendshipDao.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new NotFoundException("Нельзя удалить самого себя");
        }
        /*Проверка, существуют ли пользователи*/
        friendshipDao.deleteFriend(id, friendId);
    }

    public List<User> getAllUserFriends(long id) {
        return userDbStorage.getAllUserFriends(id);
    }

    public List<User> getCommonFriends(long firstUserId, long secondUserId) {
        return friendshipDao.getCommonFriends(firstUserId, secondUserId);
    }
}
