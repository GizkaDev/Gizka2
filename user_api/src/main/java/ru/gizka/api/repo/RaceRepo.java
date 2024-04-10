package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.race.Race;

import java.util.Optional;

@Repository
public interface RaceRepo extends JpaRepository<Race, Long> {
    Optional<Race> findByName(String name);
}
