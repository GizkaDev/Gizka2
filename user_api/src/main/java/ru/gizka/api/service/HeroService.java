package ru.gizka.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.repo.HeroRepo;
import ru.gizka.api.service.fightLogic.AttributeCalculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@Slf4j
public class HeroService {
    @Value("${death.period.seconds}")
    private String period;
    private final HeroRepo heroRepo;
    private final AttributeCalculator attributeCalculator;

    @Autowired
    public HeroService(HeroRepo heroRepo,
                       AttributeCalculator attributeCalculator) {
        this.heroRepo = heroRepo;
        this.attributeCalculator = attributeCalculator;
    }

    @Transactional
    public Hero create(Hero hero, AppUser appUser, Race race) {
        Optional<Hero> lastHero = heroRepo.findTopByAppUserLoginOrderByCreatedAtDesc(appUser.getLogin());
        if (lastHero.isEmpty() || lastHero.get().getStatus().equals(Status.DEAD)) {
            if (lastHero.isPresent() && new Date().getTime() - lastHero.get().getCreatedAt().getTime() < TimeUnit.SECONDS.toMillis(Integer.parseInt(period))) {
                log.error("Сервис героев прервал создание героя для пользователя: {} , т.к. прошло мало времени с момента смерти последнего героя ({})",
                        appUser.getLogin(), lastHero.get().getCreatedAt().getTime());
                throw new IllegalArgumentException(String.format("Прошло мало времени с момента смерти последнего героя (%s)",
                        lastHero.get().getCreatedAt().getTime()));
            }
            log.info("Сервис героев создает нового героя: {} для пользователя: {}", hero, appUser.getLogin());
            List<Duel> duels = new ArrayList<>();
            hero.setDuels(duels);
            hero.setAppUser(appUser);
            hero.setRace(race);
            hero.setStr(hero.getStr() + race.getStrBonus());
            hero.setDex(hero.getDex() + race.getDexBonus());
            hero.setCon(hero.getCon() + race.getConBonus());
            hero.setWis(hero.getWis() + race.getWisBonus());
            hero.setStatus(Status.ALIVE);
            hero.setCreatedAt(new Date());
            attributeCalculator.calculateForNew(hero);
        } else {
            log.error("Сервис героев прервал создание героя для пользователя: {} , т.к. у пользователя есть герой: {} со статусом ALIVE",
                    appUser.getLogin(), String.format("%s %s", lastHero.get().getName(), lastHero.get().getLastname()));
            throw new IllegalArgumentException(String.format("У пользователя: %s есть герой: %s со статусом ALIVE",
                    appUser.getLogin(), String.format("%s %s", lastHero.get().getName(), lastHero.get().getLastname())));
        }
        return heroRepo.save(hero);
    }

    public List<Hero> getAliveByUser(AppUser appUser) {
        log.info("Сервис героев ищет текущего героя для пользователя: {}", appUser.getLogin());
        return heroRepo.findAllByLoginAndAlive(appUser.getLogin());
    }

    public Boolean checkOwner(Long id, AppUser appUser) {
        log.info("Сервис героев проверяет, является ли пользователь: {} владельцем героя: {}", appUser.getLogin(), id);
        return heroRepo.isOwner(id, appUser.getLogin());
    }

    public Hero getByIdWithDuels(Long id) {
        log.info("Сервис героев ищет героя id: {}", id);
        Hero hero = heroRepo.findByIdWithDuels(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Герой с id: %d не найден", id))
        );
        hero.setDuels(hero.getDuels());
        return hero;
    }

    @Transactional
    public Hero save(Hero hero) {
        log.info("Сервис героев сохраняет героя id: {}", hero.getId());
        return heroRepo.save(hero);
    }
}
