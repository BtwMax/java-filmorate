package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {

    void addFilmGenre(Film film);

    void removeFilmGenre(Film film);

    Set<Genre> getAllFilmGenres(long id);


}
