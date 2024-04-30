package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.actionLogic.HeroActionLogic;
import ru.gizka.api.service.race.RaceService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.HeroValidator;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HeroFacade {

    @Value("${treat.period.seconds}")
    private String period;
    private final HeroService heroService;
    private final DtoConverter dtoConverter;
    private final HeroValidator heroValidator;
    private final RaceService raceService;
    private final HeroActionLogic heroActionLogic;

    @Autowired
    public HeroFacade(HeroService heroService,
                      DtoConverter dtoConverter,
                      HeroValidator heroValidator,
                      RaceService raceService,
                      HeroActionLogic heroActionLogic) {
        this.heroService = heroService;
        this.dtoConverter = dtoConverter;
        this.heroValidator = heroValidator;
        this.raceService = raceService;
        this.heroActionLogic = heroActionLogic;
    }

    public ResponseHeroDto create(AppUser appUser, RequestHeroDto heroDto, BindingResult bindingResult) {
        log.info("Сервис героев начинает создание нового героя: {}", heroDto);
        Optional<Race> mbRace = raceService.getByName(heroDto.getRace());
        checkValues(heroDto, bindingResult, mbRace);
        Hero hero = heroService.create(dtoConverter.getModel(heroDto), appUser, mbRace.get());
        return dtoConverter.getResponseDto(hero);
    }

    public List<ResponseHeroDto> getCurrent(AppUser appUser) {
        log.info("Сервис героев начинает поиск текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        return heroes.stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }

    public ResponseHeroDto treat(AppUser appUser) {
        log.info("Сервис героев начинает поиск текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        if (heroes.get(0).getTreatAt() == null || new Date().getTime() - heroes.get(0).getTreatAt().getTime() >= TimeUnit.SECONDS.toMillis(Integer.parseInt(period))) {
            Hero hero = heroActionLogic.treat(heroes.get(0));
            heroService.save(hero);
            return dtoConverter.getResponseDto(hero);
        }
        throw new IllegalStateException(String.format("Перевязывать раны можно только раз в сутки. Время последнего применения %s", heroes.get(0).getTreatAt()));
    }

    private void checkValues(RequestHeroDto heroDto,
                             BindingResult bindingResult,
                             Optional<Race> mbRace) {
        log.info("Сервис героев начинает проверку валидности нового героя: {}", heroDto);
        heroValidator.validate(heroDto, bindingResult);
        if (mbRace.isEmpty()) {
            bindingResult.rejectValue("", "", "Использована несуществующая раса");
            log.error("Сервис героев сообщает, что для создания героя использована несуществующая раса: {}", heroDto.getRace());
        } else {
            if (!mbRace.get().getIsPlayable()) {
                bindingResult.rejectValue("", "", "Использована неиграбельная раса");
                log.error("Сервис героев сообщает, что для создания героя использована неиграбельная раса: {}", heroDto.getRace());
            }
        }
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
