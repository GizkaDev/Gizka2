package ru.gizka.api.facade.old;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.old.race.RequestRaceDto;
import ru.gizka.api.dto.old.race.ResponseRaceDto;
import ru.gizka.api.model.old.race.Race;
import ru.gizka.api.service.old.race.RaceService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.RaceValidator;

import java.util.List;

@Service
@Slf4j
public class RaceFacade {
    private final RaceService raceService;
    private final DtoConverter dtoConverter;
    private final RaceValidator raceValidator;

    @Autowired
    public RaceFacade(RaceService raceService,
                      DtoConverter dtoConverter,
                      RaceValidator raceValidator) {
        this.raceService = raceService;
        this.dtoConverter = dtoConverter;
        this.raceValidator = raceValidator;
    }

    public ResponseRaceDto getByName(String name) {
        log.info("Сервис рас начинает поиск расы: {}", name);
        Race race = raceService.getByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Раса не найдена: %s", name)));
        return dtoConverter.getResponseDto(race);
    }

    public ResponseRaceDto create(RequestRaceDto raceDto, BindingResult bindingResult) {
        log.info("Сервис рас начинает создание новой расы: {}", raceDto.getName());
        checkValues(raceDto, bindingResult);
        Race raceToSave = dtoConverter.getModel(raceDto);
        Race savedRace = raceService.create(raceToSave);
        return dtoConverter.getResponseDto(savedRace);
    }

    public List<ResponseRaceDto> getAll() {
        log.info("Сервис рас начинает поиск все рас");
        return raceService.getAll().stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }

    private void checkValues(RequestRaceDto raceDto,
                             BindingResult bindingResult) {
        log.info("Сервис рас начинает проверку валидности новой расы: {}", raceDto.getName());
        raceValidator.validate(raceDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
