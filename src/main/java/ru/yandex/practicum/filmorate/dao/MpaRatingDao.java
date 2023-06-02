package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaRatingDao implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MpaRating> getAllRatings() {
        String sqlQuery = "SELECT * FROM ratings_mpa " +
                "ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowMpaRating);
    }

    @Override
    public MpaRating getMpaRatingById(Long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa " +
                "WHERE id = ?", id);
        if (!sqlRowSet.first()) {
            throw new NotFoundException(String.format("Рейтинг с id = %d не найден", id));
        }
        return new MpaRating(
                sqlRowSet.getLong("id"),
                sqlRowSet.getString("name")
        );
    }

    private MpaRating mapRowMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
