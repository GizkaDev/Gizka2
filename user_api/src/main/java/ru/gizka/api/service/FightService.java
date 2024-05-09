package ru.gizka.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.repo.FightRepo;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FightService {
    private final FightRepo fightRepo;

    @Autowired
    public FightService(FightRepo fightRepo) {
        this.fightRepo = fightRepo;
    }

    @Transactional
    public Fight save(Fight fight) {
        log.info("Сервис сражений сохраняет сражение между: {} {}({}) и: {}",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin(),
                fight.getCreature().getName());
        return fightRepo.save(fight);
    }

    public Optional<Fight> getLatest(Hero hero) {
        log.info("Сервис сражений получает последнее сражение героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return fightRepo.findTopByHeroIdOrderByCreatedAtDesc(hero.getId());
    }
}
