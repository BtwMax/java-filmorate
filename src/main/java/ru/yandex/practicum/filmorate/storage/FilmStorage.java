package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    void addFilm(Film film);

    Film getFilmById(long id);

    void updateFilm(Film film);

    void removeFilm(long id);

    Collection<Film> getAllFilms();
}
