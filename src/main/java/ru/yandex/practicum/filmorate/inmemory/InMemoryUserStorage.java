package ru.yandex.practicum.filmorate.inmemory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Qualifier("userStorage")
public class InMemoryUserStorage implements UserStorage {


    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public void addUser(User user) {
        validate(user);
        /*Генератор айди*/
        if (user.getId() == 0) {
            user.setId(generatorId());
        }
        users.put(user.getId(), user);
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public void updateUser(User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ServerException("Невозможно обновить несуществующего пользователя");
        }

        users.put(user.getId(), user);
    }

    @Override
    public void removeUser(long id) {
        users.remove(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private long generatorId() {
        return id++;
    }

    public void userExistsById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private void validate(User user) {
        /*Паттерн для проверки строки на соответствие Email*/
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(user.getEmail());
        /*Если имя null, то засетить логин в имя*/
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        /*Если имя пустое или пробел, то засетить логин в имя*/
        if (user.getName().isBlank() || user.getName().equals("") || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        /*Генератор айди*/
        if (user.getId() == 0) {
            user.setId(generatorId());
        }
        /*Проверка Email на корректность*/
        if (user.getEmail().equals("") || user.getEmail().isBlank() || user.getEmail().isEmpty() || !mat.matches()) {
            throw new ValidationException("Ошибка валидации Email");
        }
        /*Проверка логина на корректность*/
        if (user.getLogin().equals("") || user.getLogin().isBlank()) {
            throw new ValidationException("Ошибка валидации логина");
        }
        /*Проверка даты рождения на корректность*/
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка валидации дня рождения");
        }
    }
}
