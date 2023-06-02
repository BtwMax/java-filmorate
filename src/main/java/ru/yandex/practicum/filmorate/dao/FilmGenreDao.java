package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmGenreDao implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmGenre(Film film) {
        if (film.getGenres() != null) {
            final ArrayList<Genre> genreList = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate(
                    "insert into FILM_GENRES (FILM_ID, GENRE_ID) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genreList.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genreList.size();
                        }
                    });
        }
    }

    @Override
    public void removeFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
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
