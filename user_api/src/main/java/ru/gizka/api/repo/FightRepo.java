package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.fight.Duel;

import java.util.List;

@Repository
public interface FightRepo extends JpaRepository<Duel, Long> {

    @EntityGraph(attributePaths = {"heroes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT h.duels FROM Hero h WHERE h.id = :id ORDER BY h.createdAt DESC")
    List<Duel> findAllDuelsByHeroId(@Param("id") Long id);
}
