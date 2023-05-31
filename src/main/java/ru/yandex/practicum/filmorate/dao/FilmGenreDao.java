package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    public void addFilmGenre(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    public void removeFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public Set<Genre> getAllFilmGenres(long id) {
        String sqlQuery = "SELECT g.id, g.name FROM film_genres AS fg " +
                "INNER JOIN genres AS g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowGenre, id));
    }

    private Genre mapRowGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
