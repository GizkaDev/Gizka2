package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.CreatureService;
import ru.gizka.api.service.FightService;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.notification.NotificationBuilder;
import ru.gizka.api.service.notification.NotificationService;
import ru.gizka.api.service.fightLogic.FightLogic;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class FightFacade {
    private final FightLogic fightLogic;
    private final HeroService heroService;
    private final CreatureService creatureService;
    private final DtoConverter dtoConverter;
    private final FightService fightService;
    private final NotificationService notificationService;
    private final NotificationBuilder notificationBuilder;

    @Autowired
    public FightFacade(FightLogic fightLogic,
                       HeroService heroService,
                       CreatureService creatureService,
                       DtoConverter dtoConverter,
                       FightService fightService,
                       NotificationService notificationService,
                       NotificationBuilder notificationBuilder) {
        this.fightLogic = fightLogic;
        this.heroService = heroService;
        this.creatureService = creatureService;
        this.dtoConverter = dtoConverter;
        this.fightService = fightService;
        this.notificationService = notificationService;
        this.notificationBuilder = notificationBuilder;
    }

    public ResponseEntity<FightDto> simulate(AppUser appUser, String name) {
        log.info("Сервис сражений начинает симуляцию сражения для пользователя: {} и моба: {}", appUser.getLogin(), name);
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        Creature creature = creatureService.getByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException("Моб с таким названием не найден"));
        Fight fight = fightLogic.simulate(heroes.get(0), creature);
        saveRelation(fight, appUser);
        return new ResponseEntity<>(dtoConverter.getResponseDto(fight), HttpStatus.CREATED);
    }

    @Transactional
    private void saveRelation(Fight fight, AppUser appUser) {
        fightService.save(fight);
        Notification notification = notificationBuilder.buildForFight(fight);
        notificationService.save(notification, appUser);

        Hero afterFightHero = fight.getHero();
        if (afterFightHero.getCurrentHp() <= 0) {
            afterFightHero.setStatus(Status.DEAD);
            afterFightHero.setCurrentHp(1);
            notificationService.save(notificationBuilder.buildForDeath(afterFightHero), afterFightHero.getAppUser());
        }

        heroService.save(fight.getHero());
    }
}
