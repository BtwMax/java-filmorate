package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDao implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilm(Film film) {
        String sqlQuery = "INSERT INTO film(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setObject(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long idKey = keyHolder.getKey().longValue();
        film.setId(idKey);
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM film " +
                "WHERE id = ?", id);
        if (!sqlRowSet.first()) {
            throw new NotFoundException("Фильм с ID=" + id + " не найден!");
        }
        return new Film(
                sqlRowSet.getLong("id"),
                sqlRowSet.getString("name"),
                sqlRowSet.getString("description"),
                sqlRowSet.getDate("release_Date").toLocalDate(),
                sqlRowSet.getInt("duration"),
                new HashSet<>(),
                new MpaRating(sqlRowSet.getLong("rating_id"), null),
                null
        );
    }

    @Override
    public void updateFilm(Film film) {
        long filmId = film.getId();
        if (getFilmById(filmId) == null) {
            throw new NotFoundException("Невозможно обновить несуществующий фильм");
        }
        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
    }

    @Override
    public void removeFilm(long id) {
        String sqlQuery = "DELETE FROM film " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(),
                new MpaRating(rs.getLong("rating_id"), null),
                null
        ));
    }

    public List<Film> getPopularFilms(Integer count) {
        String getPopularQuery = "SELECT id, name, description, release_date, duration, rating_id " +
                "FROM film AS f LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";

        return jdbcTemplate.query(getPopularQuery, (rs, rowNum) -> new Film(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(),
                        new MpaRating(rs.getLong("rating_id"), null),
                        null),
                count);
    }
}
