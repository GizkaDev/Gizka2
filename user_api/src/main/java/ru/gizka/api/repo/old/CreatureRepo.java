package ru.gizka.api.repo.old;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.old.creature.Creature;

import java.util.Optional;

@Repository
public interface CreatureRepo extends JpaRepository<Creature, Long> {
    Optional<Creature> findByName(String name);
}
