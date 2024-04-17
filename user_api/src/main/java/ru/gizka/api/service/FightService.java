package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.repo.FightRepo;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FightService {
    private final FightRepo fightRepo;

    @Autowired
    public FightService(FightRepo fightRepo,
                        HeroService heroService) {
        this.fightRepo = fightRepo;
    }

    @Transactional
    public Fight save(Fight fight) {
        log.info("Сервис сражений сохраняет сражение между: {} {}({}) и: {}",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin(),
                fight.getCreature().getName());
        return fightRepo.save(fight);
    }
}
