package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.repo.DuelRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DuelService {
    private final DuelRepo duelRepo;

    @Autowired
    public DuelService(DuelRepo duelRepo) {
        this.duelRepo = duelRepo;
    }

    @Transactional
    public Duel save(Duel duel) {
        log.info("Сервис дуэлей сохраняет сражение между: {} {}({}) и: {} {}({})",
                duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin(),
                duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin());
        return duelRepo.save(duel);
    }

    public List<Duel> getAllDuelsByHeroIdSortedByDate(Long id) {
        log.info("Сервис дуэлей ищет сражения для героя id: {}", id);
        return duelRepo.findAllDuelsByHeroIdSortedByDate(id);
    }
}
