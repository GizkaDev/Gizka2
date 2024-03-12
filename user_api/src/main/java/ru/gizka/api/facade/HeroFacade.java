package ru.gizka.api.facade;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.RequestHeroDto;
import ru.gizka.api.dto.ResponseHeroDto;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.model.Hero;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.HeroValidator;

import java.util.List;

@Service
@Slf4j
public class HeroFacade {
    private final HeroService heroService;
    private final DtoConverter dtoConverter;
    private final HeroValidator heroValidator;

    @Autowired
    public HeroFacade(HeroService heroService,
                      DtoConverter dtoConverter,
                      HeroValidator heroValidator) {
        this.heroService = heroService;
        this.dtoConverter = dtoConverter;
        this.heroValidator = heroValidator;
    }

    public ResponseHeroDto create(AppUser appUser, RequestHeroDto heroDto, BindingResult bindingResult) {
        log.info("Сервис героев начинает создание нового героя: {}", heroDto);
        checkValues(heroDto, bindingResult);
        Hero hero = heroService.create(dtoConverter.getModel(heroDto), appUser);
        return dtoConverter.getResponseDto(hero);
    }

    private void checkValues(RequestHeroDto heroDto,
                             BindingResult bindingResult) {
        log.info("Сервис героев начинает проверку валидности нового героя: {}", heroDto);
        heroValidator.validate(heroDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
