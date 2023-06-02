package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmDao filmDao;
    private final LikeStorage likeDao;
    private final FilmGenreStorage filmGenre;
    private final MpaRatingStorage mpa;
    private final GenreStorage genreDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmDao filmDao,
                       LikeStorage likeDao, FilmGenreStorage filmGenre, MpaRatingStorage mpa, GenreStorage genreDao) {
        this.filmStorage = filmStorage;
        this.filmDao = filmDao;
        this.likeDao = likeDao;
        this.filmGenre = filmGenre;
        this.mpa = mpa;
        this.genreDao = genreDao;
    }

    public void addFilm(Film film) {
        filmStorage.addFilm(film);
        if (film.getGenres() != null) {
            filmGenre.addFilmGenre(film);
        }
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        film.setLikes(likeDao.getFilmLikes(id));
        film.setGenres(filmGenre.getAllFilmGenres(id));
        film.setMpa(mpa.getMpaRatingById(film.getMpa().getId()));
        return film;
    }

    public void updateFilm(Film film) {
        filmStorage.updateFilm(film);
        film.setMpa(mpa.getMpaRatingById(film.getMpa().getId()));
        film.setLikes(likeDao.getFilmLikes(film.getId()));
        if (film.getGenres() != null) {
            Collection<Genre> sortGenres = film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(new LinkedHashSet<>(sortGenres));
            for (Genre genre : film.getGenres()) {
                genre.setName(genreDao.getGenreById(genre.getId()).getName());
            }
        }
        filmGenre.removeFilmGenre(film);
        filmGenre.addFilmGenre(film);
    }

    public void removeFilm(long id) {
        filmStorage.removeFilm(id);
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        films.forEach(film -> {
            film.setLikes(likeDao.getFilmLikes(film.getId()));
            film.setGenres(filmGenre.getAllFilmGenres(film.getId()));
            film.setMpa(mpa.getMpaRatingById(film.getMpa().getId()));
        });
        return films;
    }

    public void addLike(long filmId, long userId) {
        likeDao.addLike(filmId, userId);
    }

    public void removeUserLike(long filmId, long userId) {
        likeDao.removeUserLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmDao.getPopularFilms(count);
        films.forEach(film -> {
            film.setLikes(likeDao.getFilmLikes(film.getId()));
            film.setGenres(filmGenre.getAllFilmGenres(film.getId()));
            film.setMpa(mpa.getMpaRatingById(film.getMpa().getId()));
        });
        return films;
    }
}
