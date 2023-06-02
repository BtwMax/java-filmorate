package ru.yandex.practicum.filmorate.conrtollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@RestController
@Slf4j
public class GenreController {

    private final GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable("id") Integer id) {
        log.info("Запрос на вывод жанра с id = " + id);
        return genreDao.getGenreById(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        log.info("Запрос на вывод всех жанров");
        return genreDao.getAllGenres();
    }
}

