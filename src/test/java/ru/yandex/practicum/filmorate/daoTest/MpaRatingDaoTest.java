package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.MpaRating;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testSchema.sql",
        "file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testData.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MpaRatingDaoTest {

    private final MpaRatingDao mpa;

    @Test
    public void getAllRatingsTest() {
        Assertions.assertNotNull(mpa.getAllRatings());
        Assertions.assertEquals(5, mpa.getAllRatings().size());
    }

    @Test
    public void getMpaByIdTest() {
        MpaRating mpaRating = mpa.getMpaRatingById(1L);
        Assertions.assertNotNull(mpaRating);
        Assertions.assertEquals(new MpaRating(1L, "G"), mpaRating);
    }
}
