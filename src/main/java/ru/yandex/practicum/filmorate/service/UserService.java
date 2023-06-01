package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;


@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserDao userDao;
    private final FriendshipStorage friendshipDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserDao userDao,
                       FriendshipStorage friendshipDao) {
        this.userStorage = userStorage;
        this.userDao = userDao;
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
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        friendshipDao.addFriend(user.getId(), friend.getId());
    }

    public void deleteFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new NotFoundException("Нельзя удалить самого себя");
        }
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        friendshipDao.deleteFriend(user.getId(), friend.getId());
    }

    public List<User> getAllUserFriends(long id) {
        return userDao.getAllUserFriends(id);
    }

    public List<User> getCommonFriends(long firstUserId, long secondUserId) {
        List<User> friends = userDao.getAllUserFriends(firstUserId);
        friends.retainAll(userDao.getAllUserFriends(secondUserId));
        return friends;
    }
}
