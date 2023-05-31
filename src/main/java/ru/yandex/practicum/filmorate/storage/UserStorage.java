package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    void addUser(User user);

    User getUserById(long id);

    void updateUser(User user);

    void removeUser(long id);

    Collection<User> getAllUsers();
}
