package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.fight.Fight;

import java.util.List;

@Repository
public interface FightRepo extends JpaRepository<Fight, Long> {

    @EntityGraph(attributePaths = {"heroes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT h.fights FROM Hero h WHERE h.id = :id")
    List<Fight> findAllByHeroId(@Param("id") Long id);
}
