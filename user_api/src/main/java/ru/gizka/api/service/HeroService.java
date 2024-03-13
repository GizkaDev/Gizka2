package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.model.Hero;
import ru.gizka.api.model.Status;
import ru.gizka.api.repo.HeroRepo;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class HeroService {
    private final HeroRepo heroRepo;

    @Autowired
    public HeroService(HeroRepo heroRepo) {
        this.heroRepo = heroRepo;
    }

    @Transactional
    public Hero create(Hero hero, AppUser appUser) {
        List<Hero> heroes = heroRepo.findAllByLoginAndAlive(appUser.getLogin());
        if (heroes.isEmpty()) {
            log.info("Сервис героев создает нового героя {} для пользователя: {}", hero, appUser.getLogin());
            hero.setAppUser(appUser);
            hero.setStatus(Status.ALIVE);
            hero.setCreatedAt(new Date());
        } else {
            log.error("Сервис героев прервал создание героя для пользователя: {} , т.к. у пользователя есть герой: {} со статусом ALIVE",
                    appUser.getLogin(), String.format("%s %s", heroes.get(0).getName(), heroes.get(0).getLastname()));
            throw new IllegalArgumentException(String.format("У пользователя: %s есть герой: %s со статусом ALIVE",
                    appUser.getLogin(), String.format("%s %s", heroes.get(0).getName(), heroes.get(0).getLastname())));
        }
        return heroRepo.save(hero);
    }

    public List<Hero> getAliveByUser(AppUser appUser) {
        return heroRepo.findAllByLoginAndAlive(appUser.getLogin());
    }
}
