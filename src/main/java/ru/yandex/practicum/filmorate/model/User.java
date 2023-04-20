package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private long id;
    @NotNull(message = "Email не может быть не задан")
    @NotBlank(message = "Email не может быть пустым")
    @Email
    private String email;
    @NotNull(message = "Логин не может быть не задан")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S*", message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
