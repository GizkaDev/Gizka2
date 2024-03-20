package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Duel;
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
    public Duel save(Duel duel) {
        log.info("Сервис сражений сохраняет сражение между: {} {}({}) и: {} {}({})",
                duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin(),
                duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin());
        return fightRepo.save(duel);
    }

    public List<Duel> getAllDuelsByHeroIdSortedByDate(Long id) {
        log.info("Сервис сражений ищет сражения для героя id: {}", id);
        return fightRepo.findAllDuelsByHeroIdSortedByDate(id);
    }
}
