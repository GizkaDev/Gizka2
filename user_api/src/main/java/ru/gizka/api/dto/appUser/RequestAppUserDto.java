package ru.gizka.api.dto.appUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAppUserDto {

    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$",
            message = "Логин может состоять только из букв латинского алфавита, цифр и специальных символов(_.-)")
    @Size(min = 4, max = 255, message = "Логин должен состоять минимум из 4 символов")
    @NotBlank(message = "Логин должен состоять минимум из 4 символов")
    private String login;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, " +
                    "цифр, " +
                    "а так же из специальных символов " +
                    "(@, $, !, %, *, ?, &)")
    @Size(min = 8, max = 255, message = "Пароль должен состоять минимум из 8 символов")
    @NotBlank(message = "Пароль должен состоять минимум из 8 символов")
    private String password;
}
