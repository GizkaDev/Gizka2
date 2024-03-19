package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.repo.FightRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FightService {
    private final FightRepo fightRepo;
    private final HeroService heroService;

    @Autowired
    public FightService(FightRepo fightRepo,
                        HeroService heroService) {
        this.fightRepo = fightRepo;
        this.heroService = heroService;
    }

    @Transactional
    public Fight save(Fight fight) {
        log.info("Сервис сражений сохраняет сражение между: {} {}({}) и: {} {}({})",
                fight.getHeroes().get(0).getName(), fight.getHeroes().get(0).getLastname(), fight.getHeroes().get(0).getAppUser().getLogin(),
                fight.getHeroes().get(1).getName(), fight.getHeroes().get(1).getLastname(), fight.getHeroes().get(1).getAppUser().getLogin());
        return fightRepo.save(fight);
    }

    public List<Fight> getAllByHeroId(Long id) {
        log.info("Сервис сражений ищет сражения для героя id: {}", id);
        return fightRepo.findAllByHeroId(id);
    }
}
