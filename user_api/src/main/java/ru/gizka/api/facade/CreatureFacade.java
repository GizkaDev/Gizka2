package ru.gizka.api.facade;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.creature.ResponseCreatureDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.service.CreatureService;
import ru.gizka.api.service.race.RaceService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.CreatureValidator;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CreatureFacade {
    private final CreatureService creatureService;
    private final RaceService raceService;
    private final CreatureValidator creatureValidator;
    private final DtoConverter dtoConverter;

    @Autowired
    public CreatureFacade(CreatureService creatureService,
                          RaceService raceService,
                          CreatureValidator creatureValidator,
                          DtoConverter dtoConverter) {
        this.creatureService = creatureService;
        this.raceService = raceService;
        this.creatureValidator = creatureValidator;
        this.dtoConverter = dtoConverter;
    }

    public ResponseCreatureDto create(RequestCreatureDto creatureDto, BindingResult bindingResult) {
        log.info("Сервис мобов начинает создание моба: {}", creatureDto);
        Optional<Race> mbRace = raceService.getByName(creatureDto.getRace());
        checkValues(creatureDto, bindingResult, mbRace);
        Creature creature = creatureService.create(dtoConverter.getModel(creatureDto), mbRace.get());
        return dtoConverter.getResponseDto(creature);
    }

    private void checkValues(RequestCreatureDto creatureDto,
                             BindingResult bindingResult,
                             Optional<Race> mbRace) {
        log.info("Сервис мобов начинает проверку валидности нового моба: {}", creatureDto);
        creatureValidator.validate(creatureDto, bindingResult);
        if (mbRace.isEmpty()) {
            bindingResult.rejectValue("", "", "Использована несуществующая раса");
            log.error("Сервис мобов сообщает, что для создания моба использована несуществующая раса: {}", creatureDto.getRace());
        }
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
