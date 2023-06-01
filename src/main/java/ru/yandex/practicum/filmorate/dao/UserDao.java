package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDao implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addUser(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.checkName(user.getName()));
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long idKey = keyHolder.getKey().longValue();
        user.setId(idKey);
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        if (!userRows.first()) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден!");
        }
        return new User(
                userRows.getLong("id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday").toLocalDate());
    }

    @Override
    public void updateUser(User user) {
        long userId = user.getId();
        if (getUserById(userId) == null) {
            throw new NotFoundException("Невозможно обновить несуществующего пользователя");
        }
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public void removeUser(long id) {
        String sqlQuery = "DELETE FROM users " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public List<User> getAllUserFriends(long id) {
        String sqlQuery = "SELECT friend_id, email, login, name, birthday FROM friends AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.id WHERE f.user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new User(
                        rs.getLong("friend_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate()),
                id
        );
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
