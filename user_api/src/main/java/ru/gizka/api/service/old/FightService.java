package ru.gizka.api.service.old;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.old.fight.Fight;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.repo.old.FightRepo;

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
        log.info("Сохраняем сражение между: {} {}({}) и: {}",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin(),
                fight.getCreature().getName());
        return fightRepo.save(fight);
    }

    public Fight getLatest(Hero hero) {
        log.info("Ищем последнее сражение героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return fightRepo.findTopByHeroIdOrderByCreatedAtDesc(hero.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Сражение не найдено"));
    }
}
