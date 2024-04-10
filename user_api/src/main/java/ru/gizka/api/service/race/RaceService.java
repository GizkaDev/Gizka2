package ru.gizka.api.service.race;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.repo.RaceRepo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class RaceService {
    private final RaceRepo raceRepo;

    @Autowired
    public RaceService(RaceRepo raceRepo) {
        this.raceRepo = raceRepo;
    }

    public Optional<Race> getByName(String name) {
        log.info("Сервис рас ищет расу: {}", name);
        return raceRepo.findByName(name);
    }

    @Transactional
    public Race create(Race race) {
        log.info("Сервис рас создает новую расу: {}", race.getName());
        race.setCreatedAt(new Date());
        return raceRepo.save(race);
    }

    public List<Race> getAll(){
        log.info("Сервис рас ищет все расы");
        return raceRepo.findAll();
    }
}
