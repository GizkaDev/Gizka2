package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.race.Race;

import java.util.Optional;

@Repository
public interface CreatureRepo extends JpaRepository<Creature, Long> {
    Optional<Creature> findByName(String name);
}
