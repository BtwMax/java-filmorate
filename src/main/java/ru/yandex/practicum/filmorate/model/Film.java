package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.AfterCinemaBirthday;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {

    private long id;
    @NotNull(message = "Название фильма не может быть не задано")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания фильма должна быть не больше 200 символов")
    private String description;
    @AfterCinemaBirthday
    private LocalDate releaseDate;
    @Positive(message = "Длина фильма должна быть больше 0")
    private int duration;
}
