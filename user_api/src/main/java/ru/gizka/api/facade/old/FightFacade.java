package ru.gizka.api.facade.old;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.old.fight.FightDto;
import ru.gizka.api.model.old.creature.Creature;
import ru.gizka.api.model.old.fight.Fight;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.old.hero.Status;
import ru.gizka.api.model.old.item.ItemPattern;
import ru.gizka.api.model.old.item.armor.ArmorObject;
import ru.gizka.api.model.old.item.armor.ArmorPattern;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.old.CreatureService;
import ru.gizka.api.service.old.FightService;
import ru.gizka.api.service.old.HeroService;
import ru.gizka.api.service.old.actionLogic.HeroActionLogic;
import ru.gizka.api.service.old.item.ItemObjectService;
import ru.gizka.api.service.old.item.ItemPatternService;
import ru.gizka.api.service.old.item.armor.ArmorObjectService;
import ru.gizka.api.service.old.notification.NotificationBuilder;
import ru.gizka.api.service.old.notification.NotificationService;
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
    private final ArmorObjectService armorObjectService;

    @Autowired
    public FightFacade(HeroService heroService,
                       CreatureService creatureService,
                       DtoConverter dtoConverter,
                       FightService fightService,
                       NotificationService notificationService,
                       NotificationBuilder notificationBuilder,
                       HeroActionLogic heroActionLogic,
                       ItemPatternService itemPatternService,
                       ItemObjectService itemObjectService,
                       ArmorObjectService armorObjectService) {
        this.heroService = heroService;
        this.creatureService = creatureService;
        this.dtoConverter = dtoConverter;
        this.fightService = fightService;
        this.notificationService = notificationService;
        this.notificationBuilder = notificationBuilder;
        this.heroActionLogic = heroActionLogic;
        this.itemPatternService = itemPatternService;
        this.itemObjectService = itemObjectService;
        this.armorObjectService = armorObjectService;
    }

    public ResponseEntity<FightDto> simulate(AppUser appUser, String name) {
        log.info("Начинаем симуляцию сражения для пользователя: {} и моба: {}", appUser.getLogin(), name);
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        Creature creature = creatureService.getByName(name);
        Fight fight = heroActionLogic.simulateFight(heroes.get(0), creature);
        saveNotificationAndRelation(fight, appUser);
        return new ResponseEntity<>(dtoConverter.getResponseDto(fight), HttpStatus.CREATED);
    }

    public ResponseEntity<FightDto> getLatestByAppUserForCurrentHero(AppUser appUser) {
        log.info("Начинаем поиск последнего сражения для пользователя: {}", appUser.getLogin());
        Hero hero = heroService.getLatest(appUser);
        Fight fight = fightService.getLatest(hero);
        return ResponseEntity.ok(dtoConverter.getResponseDto(fight));
    }

    private void saveNotificationAndRelation(Fight fight, AppUser appUser) {
        log.info("Начинаем готовить оповещения для пользователя: {}", appUser.getLogin());
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
        log.info("Начинаем сохранять связи для сражения");
        Fight savedFight = fightService.save(fight); //сохраняем сражение
        Hero savedHero = heroService.save(fight.getHero()); //сохраняем героя после сражения
        if (fight.getLoot() != null && !fight.getLoot().isEmpty()) {
            Long weightForAdd = 0L;
            for (ItemPattern itemPattern : fight.getLoot()) {
                ItemPattern ip = itemPatternService.getByNameWithFights(itemPattern.getName());
                ip.getFights().add(savedFight);
                itemPatternService.save(ip); //сохраняем инф. о добыче в сражении
                if (itemPattern instanceof ArmorPattern) {
                    ArmorObject ao = armorObjectService.save((ArmorPattern) ip, savedHero);
                    weightForAdd += ao.getWeight(); //добавляем объект добычи в инвентарь и складываем вес
                } else {
                    weightForAdd += itemObjectService.save(ip, savedHero).getWeight(); //добавляем объект добычи в инвентарь и складываем вес
                }
            }
            savedHero.setCurrentWeight(savedHero.getCurrentWeight() + weightForAdd);
            heroService.save(savedHero); //сохраняем героя с весом
        }
    }
}
