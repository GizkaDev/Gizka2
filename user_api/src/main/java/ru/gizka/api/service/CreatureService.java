package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.repo.CreatureRepo;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CreatureService {
    private final CreatureRepo creatureRepo;

    @Autowired
    public CreatureService(CreatureRepo creatureRepo){
        this.creatureRepo = creatureRepo;
    }

    @Transactional
    public Creature create(Creature creature, Race race){
        log.info("Сервис мобов создает нового моба: {}", creature);
        creature.setRace(race);
        creature.setCreatedAt(new Date());
        return creatureRepo.save(creature);
    }

    public Optional<Creature> getByName(String name){
        log.info("Сервис мобов ищет моба: {}", name);
        return creatureRepo.findByName(name);
    }
}
