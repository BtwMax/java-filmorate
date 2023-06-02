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
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDaoTest {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final LikeDao likeDao;
    private final FilmService filmService;
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
        filmService.addFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmDao.getFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "Film")
                );
        assertThat(filmService.getAllFilms()).hasSize(1);
        assertThat(filmService.getAllFilms()).contains(firstFilm);
    }

    @Test
    public void updateFilmTest() {
        filmService.addFilm(firstFilm);
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
        filmService.updateFilm(updateFilm);
        Film updatedFilm = filmDao.getFilmById(firstFilm.getId());
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
        filmService.addFilm(firstFilm);
        filmService.addFilm(secondFilm);
        filmDao.removeFilm(firstFilm.getId());
        Collection<Film> films = filmService.getAllFilms();

        assertThat(films).hasSize(1);
        assertThat(films).contains(secondFilm);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmDao.getFilmById(firstFilm.getId())
        );
    }

    @Test
    public void addLikeAndGetFilmLikesTest() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        filmDao.addFilm(firstFilm);
        filmDao.addFilm(secondFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());

        assertThat(likeDao.getFilmLikes(firstFilm.getId())).hasSize(2);
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(firstUser.getId());
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(secondUser.getId());
        assertThat(likeDao.getFilmLikes(secondFilm.getId())).hasSize(0);
    }

    @Test
    public void removeUserLike() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        filmDao.addFilm(firstFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());
        likeDao.removeUserLike(firstFilm.getId(), firstUser.getId());

        assertThat(likeDao.getFilmLikes(firstFilm.getId())).hasSize(1);
        assertThat(likeDao.getFilmLikes(firstFilm.getId())).contains(secondUser.getId());
    }

    @Test
    public void getPopularFilms() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        filmService.addFilm(firstFilm);
        filmService.addFilm(secondFilm);

        likeDao.addLike(firstFilm.getId(), firstUser.getId());
        likeDao.addLike(firstFilm.getId(), secondUser.getId());

        firstFilm.setLikes(likeDao.getFilmLikes(firstFilm.getId()));

        assertThat(filmService.getMostPopularFilms(5)).hasSize(2);
        assertThat(filmService.getMostPopularFilms(1)).contains(firstFilm);
    }
}
