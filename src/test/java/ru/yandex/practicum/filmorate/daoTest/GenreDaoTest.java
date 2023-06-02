package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {

    private final GenreDao genreDao;

    @Test
    public void getGenreById() {
        Genre genreId1 = genreDao.getGenreById(1);
        Assertions.assertNotNull(genreId1);
        Assertions.assertEquals(new Genre(1, "Комедия"), genreId1);
    }

    @Test
    public void getAllGenres() {
        Collection<Genre> genres = genreDao.getAllGenres();
        Assertions.assertNotNull(genres);
        Assertions.assertEquals(6, genres.size());
    }
}
