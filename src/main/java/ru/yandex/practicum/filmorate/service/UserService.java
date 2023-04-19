package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private long id = 1;

    public void addUser(User user) {
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
        users.add(user);
    }

    public void updateUser(User user) {
        int index = (int) (user.getId() - 1);
        users.set(index, user);
    }

    public List<User> getAllUsers() {
        return users;
    }

    private long generatorId() {
        return id++;
    }
}
