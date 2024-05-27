package ru.gizka.api.service.old;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.old.creature.Creature;
import ru.gizka.api.model.old.race.Race;
import ru.gizka.api.repo.old.CreatureRepo;

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
        log.info("Создаем нового моба: {}", creature);
        creature.setRace(race);
        creature.setCreatedAt(new Date());
        attributeCalculator.calculateForNew(creature);
        return creatureRepo.save(creature);
    }

    public Creature getByName(String name) {
        log.info("Ищем моба: {}", name);
        return creatureRepo.findByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException("Моб не найден"));
    }

    public Optional<Creature> getByNameOptional(String name) {
        log.info("Ищем моба: {}", name);
        return creatureRepo.findByName(name);
    }

    public List<Creature> getAll() {
        log.info("Ищем всех мобов");
        return creatureRepo.findAll();
    }
}
