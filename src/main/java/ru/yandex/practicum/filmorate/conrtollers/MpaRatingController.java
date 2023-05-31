package ru.yandex.practicum.filmorate.conrtollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@RestController
@Slf4j
class MpaRatingController {

    private final MpaRatingDao mpaRating;

    @Autowired
    MpaRatingController(MpaRatingDao mpaRating) {
        this.mpaRating = mpaRating;
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaById(@PathVariable("id") Long id) {
        log.info("Запрос на вывод рейтинга с id = " + id);
        return mpaRating.getMpaRatingById(id);
    }

    @GetMapping("/mpa")
    public Collection<MpaRating> getAllMpaRatings() {
        log.info("Запрос на вывод всех рейтингов");
        return mpaRating.getAllRatings();
    }
}
