package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Component
public class FriendshipDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FriendshipDao(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage,
                         UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            int status = 0;
            if (friend.getFriends().contains(id)) {
                status = 1;
                String sqlQuery = "UPDATE friends SET status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sqlQuery, status, friendId, id);
            }
            String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, friendId, status);
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, id, friendId);
            if (friend.getFriends().contains(id)) {
                int status = 0;
                sqlQuery = "UPDATE friends SET status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sqlQuery, status, friendId, id);
            }
        }
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> friends = userDbStorage.getAllUserFriends(firstUserId);
        friends.retainAll(userDbStorage.getAllUserFriends(secondUserId));
        return friends;
    }
}
