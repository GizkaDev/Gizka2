package ru.gizka.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.repo.HeroRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
            log.info("Сервис героев создает нового героя: {} для пользователя: {}", hero, appUser.getLogin());
            List<Fight> fights = new ArrayList<>();
            hero.setFights(fights);
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
        log.info("Сервис героев ищет текущего героя для пользователя: {}", appUser.getLogin());
        return heroRepo.findAllByLoginAndAlive(appUser.getLogin());
    }

    public Boolean checkOwner(Long id, AppUser appUser){
        log.info("Сервис героев проверяет, является ли пользователь: {} владельцем героя: {}", appUser.getLogin(), id);
        return heroRepo.isOwner(id, appUser.getLogin());
    }

    public Hero getByIdWithFights(Long id) {
        log.info("Сервис героев ищет героя id: {}", id);
        Hero hero = heroRepo.findByIdWithFights(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Герой с id: %d не найден", id))
        );
        hero.setFights(hero.getFights());
        return hero;
    }

    @Transactional
    public Hero save(Hero hero) {
        log.info("Сервис героев сохраняет героя id: {}", hero.getId());
        return heroRepo.save(hero);
    }
}
