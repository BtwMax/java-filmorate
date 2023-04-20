package ru.yandex.practicum.filmorate.conrtollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
class FilmController {
    FilmService filmService = new FilmService();

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление пользователя");
        filmService.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateUser(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление пользователя");
        filmService.updateFilm(film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAllUsers() {
        log.info("Текущее количество фильмов: {}", filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }
}
