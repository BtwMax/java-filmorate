package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilmService {

    private final List<Film> films = new ArrayList<>();
    private long id = 1;

    public void addFilm(Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Ошибка валидации названия фильма");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Ошибка валидации описания фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            throw new ValidationException("Ошибка валидации даты");
        }
        if (film.getDuration() < 1) {
            throw new ValidationException("Ошибка валидации продолжительности фильма");
        }
        if (film.getId() == 0) {
            film.setId(idGenerator());
        }
        films.add(film);
    }

    public void updateFilm(Film film) {
        int index = (int) (film.getId() - 1);
        films.set(index, film);
    }

    public List<Film> getAllFilms() {
        return films;
    }

    private long idGenerator() {
        return id++;
    }
}
