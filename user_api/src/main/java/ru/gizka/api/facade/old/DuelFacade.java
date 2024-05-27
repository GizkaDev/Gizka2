package ru.gizka.api.facade.old;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.old.fight.DuelDto;
import ru.gizka.api.model.old.fight.Duel;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.appUser.AppUserService;
import ru.gizka.api.service.old.DuelService;
import ru.gizka.api.service.old.HeroService;
import ru.gizka.api.service.old.actionLogic.HeroActionLogic;
import ru.gizka.api.service.old.notification.NotificationBuilder;
import ru.gizka.api.service.old.notification.NotificationService;
import ru.gizka.api.util.DtoConverter;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DuelFacade {
    private final HeroService heroService;
    private final AppUserService appUserService;
    private final DtoConverter dtoConverter;
    private final DuelService duelService;
    private final NotificationService notificationService;
    private final NotificationBuilder notificationBuilder;
    private final HeroActionLogic heroActionLogic;


    @Autowired
    public DuelFacade(HeroService heroService,
                      AppUserService appUserService,
                      DtoConverter dtoConverter,
                      DuelService duelService,
                      NotificationService notificationService,
                      NotificationBuilder notificationBuilder,
                      HeroActionLogic heroActionLogic) {
        this.heroService = heroService;
        this.appUserService = appUserService;
        this.dtoConverter = dtoConverter;
        this.duelService = duelService;
        this.notificationService = notificationService;
        this.notificationBuilder = notificationBuilder;
        this.heroActionLogic = heroActionLogic;
    }

    public ResponseEntity<DuelDto> simulateDuel(AppUser user1, String login) {
        log.info("Начинаем симуляцию дуэли для пользователей: {} {}", user1.getLogin(), login);
        if (user1.getLogin().equals(login)) {
            throw new IllegalArgumentException("Нельзя выбрать своего героя в качестве соперника");
        }
        AppUser user2 = appUserService.getByLogin(login);
        List<Hero> heroes1 = heroService.getAliveByUser(user1);
        List<Hero> heroes2 = heroService.getAliveByUser(user2);
        if (heroes1.isEmpty() || heroes2.isEmpty()) {
            throw new EntityNotFoundException("У одного из пользователей нет героя со статусом ALIVE.");
        }
        Duel duel = heroActionLogic.simulateDuel(heroes1.get(0), heroes2.get(0));
        saveRelation(duel, user1, user2);
        return new ResponseEntity<>(dtoConverter.getResponseDto(duel), HttpStatus.CREATED);
    }

    public ResponseEntity<List<DuelDto>> getAllDuelsForCurrentHero(AppUser appUser) {
        log.info("Начинаем поиск дуэлей для текущего героя пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (!heroes.isEmpty()) {
            List<Duel> duels = duelService.getAllDuelsByHeroIdSortedByDate(heroes.get(0).getId());
            return ResponseEntity.ok(duels.stream()
                    .map(dtoConverter::getResponseDto)
                    .toList());
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Transactional
    private void saveRelation(Duel duel, AppUser user1, AppUser user2) {
        duelService.save(duel);
        notificationService.save(notificationBuilder.buildForAttacker(duel), user1);
        notificationService.save(notificationBuilder.buildForDefender(duel), user2);
        Hero hero1 = heroService.getByIdWithDuels(duel.getHeroes().get(0).getId());
        Hero hero2 = heroService.getByIdWithDuels(duel.getHeroes().get(1).getId());
        log.info("Сервис дуэлей сохраняет связь герой-дуэль для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        hero1.getDuels().add(duel);
        hero2.getDuels().add(duel);
        //здесь косяк, видимо, лучше написать отдельный метод для сохранения в hero_duel
        heroService.save(hero1);
        heroService.save(hero2);
    }
}
