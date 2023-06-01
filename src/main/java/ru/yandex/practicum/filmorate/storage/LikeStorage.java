package ru.yandex.practicum.filmorate.storage;


import java.util.Set;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void removeUserLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);
}
