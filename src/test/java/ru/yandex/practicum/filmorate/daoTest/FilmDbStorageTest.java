package ru.yandex.practicum.filmorate.daoTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
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

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testSchema.sql",
        "file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testData.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
    public void addAndGetFilmByIdTest() {
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);
        Film film = firstFilm;
        Film film2 = secondFilm;
        Collection<Film> films = new ArrayList<>();
        films.add(film);
        films.add(film2);

        Assertions.assertEquals(film, filmDbStorage.getFilmById(firstFilm.getId()));
        Assertions.assertEquals(film2, filmDbStorage.getFilmById(secondFilm.getId()));
        Assertions.assertEquals(films.size(), filmDbStorage.getAllFilms().size());
        Assertions.assertEquals(films, filmDbStorage.getAllFilms());
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
        Film updatedFilm = filmDbStorage.getFilmById(firstFilm.getId());

        Assertions.assertEquals(updatedFilm, updatedFilm);
    }

    @Test
    public void removeFilmTest() {
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);
        filmDbStorage.removeFilm(firstFilm.getId());

        Assertions.assertEquals(1, filmDbStorage.getAllFilms().size());
        Assertions.assertThrows(
                NotFoundException.class,
                ()-> filmDbStorage.getFilmById(1)
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

        Set<Long> likes = new HashSet<>();
        likes.add(firstUser.getId());
        likes.add(secondUser.getId());

        Assertions.assertNotNull(likeDao.getFilmLikes(firstFilm.getId()));
        Assertions.assertEquals(likes, likeDao.getFilmLikes(firstFilm.getId()));
        Assertions.assertEquals(0, likeDao.getFilmLikes(secondFilm.getId()).size());
    }

    @Test
    public void removeUserLike() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());
        likeDao.removeUserLike(firstFilm.getId(), firstUser.getId());

        Set<Long> likes = new HashSet<>();
        likes.add(secondUser.getId());

        Assertions.assertNotNull(likeDao.getFilmLikes(firstFilm.getId()));
        Assertions.assertEquals(likes, likeDao.getFilmLikes(firstFilm.getId()));
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

        List<Film> popularFilms = new ArrayList<>();
        popularFilms.add(firstFilm);
        popularFilms.add(secondFilm);

        Assertions.assertEquals(popularFilms, filmDbStorage.getPopularFilms(2));
    }
}
