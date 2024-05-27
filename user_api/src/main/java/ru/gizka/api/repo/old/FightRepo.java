package ru.gizka.api.repo.old;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.old.fight.Fight;

import java.util.List;
import java.util.Optional;

@Repository
public interface FightRepo extends JpaRepository<Fight, Long> {
    @EntityGraph(attributePaths = {"heroes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT d FROM Fight d WHERE d.hero.id = :id ORDER BY d.createdAt DESC")
    List<Fight> findAllFightsByHeroIdSortedByDate(@Param("id") Long id);

    Optional<Fight> findTopByHeroIdOrderByCreatedAtDesc(Long heroId);
}
