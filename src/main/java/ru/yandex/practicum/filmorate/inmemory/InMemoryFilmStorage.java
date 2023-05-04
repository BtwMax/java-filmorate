package ru.yandex.practicum.filmorate.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public void addFilm(Film film) {
        validate(film);
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public void removeFilm(long id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private long idGenerator() {
        return id++;
    }

    private void validate(Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Ошибка валидации названия фильма");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Ошибка валидации описания фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации даты");
        }
        if (film.getDuration() < 1) {
            throw new ValidationException("Ошибка валидации продолжительности фильма");
        }
        if (film.getId() == 0) {
            film.setId(idGenerator());
        }
    }
}
