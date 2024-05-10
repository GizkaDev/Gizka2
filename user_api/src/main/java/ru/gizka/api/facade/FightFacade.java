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
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.CreatureService;
import ru.gizka.api.service.FightService;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.actionLogic.HeroActionLogic;
import ru.gizka.api.service.item.ItemObjectService;
import ru.gizka.api.service.item.ItemPatternService;
import ru.gizka.api.service.notification.NotificationBuilder;
import ru.gizka.api.service.notification.NotificationService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class FightFacade {
    private final HeroService heroService;
    private final CreatureService creatureService;
    private final DtoConverter dtoConverter;
    private final FightService fightService;
    private final NotificationService notificationService;
    private final NotificationBuilder notificationBuilder;
    private final HeroActionLogic heroActionLogic;
    private final ItemPatternService itemPatternService;
    private final ItemObjectService itemObjectService;

    @Autowired
    public FightFacade(HeroService heroService,
                       CreatureService creatureService,
                       DtoConverter dtoConverter,
                       FightService fightService,
                       NotificationService notificationService,
                       NotificationBuilder notificationBuilder,
                       HeroActionLogic heroActionLogic,
                       ItemPatternService itemPatternService,
                       ItemObjectService itemObjectService) {
        this.heroService = heroService;
        this.creatureService = creatureService;
        this.dtoConverter = dtoConverter;
        this.fightService = fightService;
        this.notificationService = notificationService;
        this.notificationBuilder = notificationBuilder;
        this.heroActionLogic = heroActionLogic;
        this.itemPatternService = itemPatternService;
        this.itemObjectService = itemObjectService;
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
        Fight fight = heroActionLogic.simulateFight(heroes.get(0), creature);
        saveNotificationAndRelation(fight, appUser);
        return new ResponseEntity<>(dtoConverter.getResponseDto(fight), HttpStatus.CREATED);
    }

    public ResponseEntity<FightDto> getLatestByAppUserForCurrentHero(AppUser appUser) {
        log.info("Сервис сражений начинает поиск последнего сражения для пользователя: {}", appUser.getLogin());
        Hero hero = heroService.getLatest(appUser).orElseThrow(
                () -> new EntityNotFoundException("У пользователя нет героев"));
        Fight fight = fightService.getLatest(hero).orElseThrow(
                () -> new EntityNotFoundException("У героя нет сражений"));
        return ResponseEntity.ok(dtoConverter.getResponseDto(fight));
    }

    private void saveNotificationAndRelation(Fight fight, AppUser appUser) {
        log.info("Сервис сражений начинает готовить оповещения для пользователя: {}", appUser.getLogin());
        //оповещение о сражении
        notificationService.save(notificationBuilder.buildForFight(fight), appUser);
        //оповещение о смерти, если она наступила
        if (fight.getHero().getCurrentHp() <= 0) {
            fight.getHero().setStatus(Status.DEAD);
            fight.getHero().setCurrentHp(1);
            try {
                Thread.sleep(5); // чтобы оповещение о сражении и оповещение о смерти (в ее случае) имели разное время создания
            } catch (InterruptedException e) {
            }
            notificationService.save(notificationBuilder.buildForDeath(fight.getHero()), appUser);
        }
        //оповещение о добыче, если она есть
        List<ItemPattern> loot = itemPatternService.getRandomLootPattern(fight);
        fight.setLoot(loot);
        if (loot != null && !loot.isEmpty()) {
            notificationService.save(notificationBuilder.buildForLoot(fight), appUser);
        }
        saveRelation(fight);
    }

    @Transactional
    private void saveRelation(Fight fight) {
        log.info("Сервис сражений начинает сохранять связи для сражения");
        Fight savedFight = fightService.save(fight); //сохраняем сражение
        Hero savedHero = heroService.save(fight.getHero()); //сохраняем героя после сражения
        if (fight.getLoot() != null && !fight.getLoot().isEmpty()) {
            Long weightForAdd = 0L;
            for (ItemPattern itemPattern : fight.getLoot()) {
                ItemPattern ip = itemPatternService.getByNameWithFights(itemPattern.getName());
                ip.getFights().add(savedFight);
                itemPatternService.save(ip); //сохраняем инф. о добыче в сражении
                weightForAdd += itemObjectService.save(ip, savedHero).getWeight(); //добавляем объект добычи в инвентарь и складываем вес
            }
            savedHero.setCurrentWeight(savedHero.getCurrentWeight() + weightForAdd);
            heroService.save(savedHero); //сохраняем героя с весом
        }
    }
}
