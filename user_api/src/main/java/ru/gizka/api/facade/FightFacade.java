package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.AppUserService;
import ru.gizka.api.service.FightService;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.fightLogic.FightLogic;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class FightFacade {
    private final FightLogic fightLogic;
    private final HeroService heroService;
    private final AppUserService appUserService;
    private final DtoConverter dtoConverter;
    private final FightService fightService;


    @Autowired
    public FightFacade(FightLogic fightLogic,
                       HeroService heroService,
                       AppUserService appUserService,
                       DtoConverter dtoConverter,
                       FightService fightService) {
        this.fightLogic = fightLogic;
        this.heroService = heroService;
        this.appUserService = appUserService;
        this.dtoConverter = dtoConverter;
        this.fightService = fightService;
    }

    public ResponseEntity<FightDto> simulateDuel(AppUser user1, String login) {
        log.info("Сервис сражений начинает симуляцию дуэли для пользователей: {} {}", user1.getLogin(), login);
        if (user1.getLogin().equals(login)) {
            throw new IllegalArgumentException("Нельзя выбрать своего героя в качестве соперника");
        }
        AppUser user2 = appUserService.getByLogin(login)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не найден"));
        List<Hero> heroes1 = heroService.getAliveByUser(user1);
        List<Hero> heroes2 = heroService.getAliveByUser(user2);
        if (heroes1.isEmpty() || heroes2.isEmpty()) {
            throw new EntityNotFoundException("У одного из пользователей нет героя со статусом ALIVE.");
        }
        Fight fight = fightLogic.simulate(heroes1.get(0), heroes2.get(0));
        return ResponseEntity.ok(dtoConverter.getResponseDto(fight));
    }

    public ResponseEntity<List<FightDto>> getAllByOwnHeroId(AppUser appUser, Long id) {
        log.info("Сервис сражений начинает поиск сражений для героя id: {} пользователя: {}", id, appUser.getLogin());
        if (heroService.checkOwner(id, appUser)) {
            List<Fight> fights = fightService.getAllByHeroId(id);
            return ResponseEntity.ok(fights.stream()
                    .map(dtoConverter::getResponseDto)
                    .toList());
        }
        throw new EntityNotFoundException(String.format("У героя %s не найден герой id: %d", appUser.getLogin(), id));
    }
}
