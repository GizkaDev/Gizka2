package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.repo.CreatureRepo;
import ru.gizka.api.service.fightLogic.AttributeCalculator;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CreatureService {
    private final CreatureRepo creatureRepo;
    private final AttributeCalculator attributeCalculator;

    @Autowired
    public CreatureService(CreatureRepo creatureRepo,
                           AttributeCalculator attributeCalculator) {
        this.creatureRepo = creatureRepo;
        this.attributeCalculator = attributeCalculator;
    }

    @Transactional
    public Creature create(Creature creature, Race race) {
        log.info("Сервис мобов создает нового моба: {}", creature);
        creature.setRace(race);
        creature.setCreatedAt(new Date());
        attributeCalculator.calculateForNew(creature);
        return creatureRepo.save(creature);
    }

    public Optional<Creature> getByName(String name) {
        log.info("Сервис мобов ищет моба: {}", name);
        return creatureRepo.findByName(name);
    }

    public List<Creature> getAll() {
        log.info("Сервис мобов ищет всех мобов");
        return creatureRepo.findAll();
    }
}
