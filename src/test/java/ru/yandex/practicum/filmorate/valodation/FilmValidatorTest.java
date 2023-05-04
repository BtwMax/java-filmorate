package ru.yandex.practicum.filmorate.valodation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.inmemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidatorTest {

    @Test
    void validateIsValidFilm() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Description", LocalDate.of(1990, 12, 1), 120);
        service.addFilm(film);
        Assertions.assertEquals(1, service.getAllFilms().size());
    }

    @Test
    void validateFilmWithBlankName() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, " ", "Description", LocalDate.of(1990, 12, 1), 120);
        Assertions.assertEquals(0, service.getAllFilms().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFilm(film));
    }

    @Test
    void validateFilmWithEmptyName() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "", "Description", LocalDate.of(1990, 12, 1), 120);
        Assertions.assertEquals(0, service.getAllFilms().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFilm(film));
    }

    @Test
    void validateFilmWithDescriptionSizeIs200() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Lorem ipsum dolor sit amet, " +
                "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna " +
                "aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tatio",
                LocalDate.of(1990, 12, 1), 120);
        service.addFilm(film);
        Assertions.assertEquals(1, service.getAllFilms().size());
    }

    @Test
    void validateFilmWithDescriptionSizeIs201() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Lorem ipsum dolor sit amet, " +
                "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna " +
                "aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tatioo",
                LocalDate.of(1990, 12, 1), 120);
        Assertions.assertEquals(0, service.getAllFilms().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFilm(film));
    }

    @Test
    void validateFilmWithReleaseDateIsBeforeCinemaBirthday() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Description", LocalDate.of(1895, 12, 27), 120);
        Assertions.assertEquals(0, service.getAllFilms().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFilm(film));
    }

    @Test
    void validateFilmWithReleaseDateIsAfterCinemaBirthday() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Description", LocalDate.of(1895, 12, 28), 120);
        service.addFilm(film);
        Assertions.assertEquals(1, service.getAllFilms().size());
    }

    @Test
    void validateFilmWithDurationIs0() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Description", LocalDate.of(1990, 12, 28), 0);
        Assertions.assertEquals(0, service.getAllFilms().size());
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.addFilm(film));
    }

    @Test
    void validateFilmWithDurationIs1() {
        InMemoryFilmStorage service = new InMemoryFilmStorage();
        Film film = new Film(1, "Film", "Description", LocalDate.of(1990, 12, 28), 1);
        service.addFilm(film);
        Assertions.assertEquals(1, service.getAllFilms().size());
    }
}
