package ru.yandex.practicum.filmorate.storage;

public interface FriendshipStorage {

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);
}
