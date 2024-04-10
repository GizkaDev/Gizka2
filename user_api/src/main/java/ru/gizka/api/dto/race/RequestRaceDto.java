package ru.gizka.api.dto.race;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRaceDto {

    @Size(min = 1, max = 100, message = "Название расы должно состоять минимум из одного символа")
    @NotBlank(message = "Название расы должно состоять минимум из одного символа")
    @Pattern(regexp = "^[а-яА-Я-]+$", message = "Название расы может состоять только из букв русского алфавита и тире ")
    private String name;

//    @NotBlank(message = "Не указана играбельность расы")
    @NotNull(message = "Не указана играбельность расы")
    private Boolean isPlayable;
}
