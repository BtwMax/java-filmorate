package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmDbStorage filmDbStorage,
                       LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.filmDbStorage = filmDbStorage;
        this.likeDao = likeDao;
    }

    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void updateFilm(Film film) {
        filmStorage.updateFilm(film);
    }

    public void removeFilm(long id) {
        filmStorage.removeFilm(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        likeDao.addLike(filmId, userId);
    }

    public void removeUserLike(long filmId, long userId) {
        likeDao.removeUserLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count);
    }
}
