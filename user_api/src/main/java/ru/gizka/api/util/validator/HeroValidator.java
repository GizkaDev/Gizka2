package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.service.race.RaceService;

import java.util.Optional;

@Component
@Slf4j
public class HeroValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestHeroDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestHeroDto heroDto = (RequestHeroDto) target;
        log.info("Валидатор героев проверяет нового героя: {}", heroDto);
        validateCreate(heroDto, errors);
    }

    private void validateCreate(RequestHeroDto heroDto, Errors errors) {
        if (heroDto.getStr() == null || heroDto.getDex() == null || heroDto.getCon() == null) {
            errors.rejectValue("", "", "Использовано слишком мало очков");
            log.error("Валидатор героев сообщает, что одно из значений характеристик null: {}", heroDto);
        } else {
            if ((heroDto.getStr() + heroDto.getDex() + heroDto.getCon()) > 30) {
                errors.rejectValue("", "", "Использовано слишком много очков");
                log.error("Валидатор героев сообщает, что для создания героя использовано слишком много очков: {}", heroDto);
            }

            if ((heroDto.getStr() + heroDto.getDex() + heroDto.getCon()) < 30) {
                errors.rejectValue("", "", "Использовано слишком мало очков");
                log.error("Валидатор героев сообщает, что для создания героя использовано слишком мало очков: {}", heroDto);
            }
        }
    }
}
