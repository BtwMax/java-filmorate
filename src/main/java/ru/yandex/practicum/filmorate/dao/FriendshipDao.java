package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FriendshipDao implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long id, Long friendId) {
        int status = 0;
        String sql = "SELECT user_id, friend_id FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        List<Map<String, Object>> friends = jdbcTemplate.queryForList(sql, friendId, id);
        List<Map<String, Object>> friends1 = jdbcTemplate.queryForList(sql, id, friendId);
        if (friends.size() == 0 || friends1.size() == 0) {
            status = 1;
            String sqlQuery = "UPDATE friends SET status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, status, friendId, id);
        }
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId, status);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        String sql = "SELECT user_id, friend_id FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        List<Map<String, Object>> friends = jdbcTemplate.queryForList(sql, friendId, id);
        if (friends.size() == 0) {
            int status = 0;
            sqlQuery = "UPDATE friends SET status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, status, friendId, id);
        }
    }
}
