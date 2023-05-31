package ru.yandex.practicum.filmorate.daoTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikeDao likeDao;
    private User firstUser;
    private User secondUser;
    private Film firstFilm;
    private Film secondFilm;

    @BeforeEach
    public void setUp() {
        firstUser = User.builder()
                .name("Qwer")
                .login("FirstUserewr")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1984, 7, 8))
                .build();
        secondUser = User.builder()
                .name("Name")
                .login("Login")
                .email("secondUser@yandex.ru")
                .birthday(LocalDate.of(1990, 10, 8))
                .build();
        firstFilm = Film.builder()
                .name("Film")
                .description("qweqwrertr")
                .releaseDate(LocalDate.of(1990, 5, 1))
                .duration(120)
                .build();
        firstFilm.setLikes(new HashSet<>());
        firstFilm.setMpa(new MpaRating(2L, "PG"));
        firstFilm.setGenres(new HashSet<>(List.of(new Genre(4, "Триллер"))));
        secondFilm = Film.builder()
                .name("Film2")
                .description("qweqw")
                .releaseDate(LocalDate.of(1999, 5, 1))
                .duration(120)
                .build();
        secondFilm.setLikes(new HashSet<>());
        secondFilm.setMpa(new MpaRating(3L, "PG-13"));
        secondFilm.setGenres(new HashSet<>(List.of(new Genre(3, "Мультфильм"))));
    }

    @Test
    public void addAndGetFilmByIdAndGetAllFilmsTest() {
        filmDbStorage.addFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "Film")
                );
        assertThat(filmDbStorage.getAllFilms()).hasSize(1);
        assertThat(filmDbStorage.getAllFilms()).contains(firstFilm);
    }

    @Test
    public void updateFilmTest() {
        filmDbStorage.addFilm(firstFilm);
        Film updateFilm = Film.builder()
                .id(firstFilm.getId())
                .name("FilmUpdate")
                .description("new Description")
                .releaseDate(LocalDate.of(1990, 5, 1))
                .duration(120)
                .build();
        updateFilm.setLikes(new HashSet<>());
        updateFilm.setMpa(new MpaRating(2L, "PG"));
        updateFilm.setGenres(new HashSet<>(List.of(new Genre(4, "Триллер"))));
        filmDbStorage.updateFilm(updateFilm);
        Film updatedFilm = filmDbStorage.getFilmById(firstFilm.getId());
        Optional<Film> testUpdateFilm = Optional.ofNullable(updatedFilm);
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "FilmUpdate")
                                .hasFieldOrPropertyWithValue("description", "new Description")
                );

    }

    @Test
    public void removeFilmTest() {
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);
        filmDbStorage.removeFilm(firstFilm.getId());
        Collection<Film> films = filmDbStorage.getAllFilms();

        assertThat(films).hasSize(1);
        assertThat(films).contains(secondFilm);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmDbStorage.getFilmById(firstFilm.getId())
        );
    }

    @Test
    public void addLikeAndGetFilmLikesTest() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());

        assertThat(likeDao.getFilmLikes(firstFilm.getId())).hasSize(2);
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(firstUser.getId());
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(secondUser.getId());
        assertThat(likeDao.getFilmLikes(secondFilm.getId())).hasSize(0);
    }

    @Test
    public void removeUserLike() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        filmDbStorage.addFilm(firstFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());
        likeDao.removeUserLike(firstFilm.getId(), firstUser.getId());

        assertThat(likeDao.getFilmLikes(firstFilm.getId())).hasSize(1);
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(secondUser.getId());
    }

    @Test
    public void getPopularFilms() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());

        firstFilm.setLikes(likeDao.getFilmLikes(firstFilm.getId()));

        assertThat(filmDbStorage.getPopularFilms(5)).hasSize(2);
        assertThat(filmDbStorage.getPopularFilms(1)).contains(firstFilm);
    }
}
