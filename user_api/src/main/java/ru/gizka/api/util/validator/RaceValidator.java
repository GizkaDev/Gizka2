package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.service.race.RaceService;

@Component
@Slf4j
public class RaceValidator implements Validator {
    private final RaceService raceService;

    @Autowired
    public RaceValidator(RaceService raceService) {
        this.raceService = raceService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return RequestRaceDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestRaceDto raceDto = (RequestRaceDto) target;
        log.info("Валидатор рас проверяет новую расу: {}", raceDto.getName());
        validateCreate(raceDto, errors);
    }

    private void validateCreate(RequestRaceDto raceDto, Errors errors) {
        if (raceService.getByName(raceDto.getName()).isPresent()) {
            errors.rejectValue("name", "", "Название расы уже существует");
            log.info("Валидатор рас сообщает, что название расы уже занято: {}", raceDto.getName());
        }
    }
}
