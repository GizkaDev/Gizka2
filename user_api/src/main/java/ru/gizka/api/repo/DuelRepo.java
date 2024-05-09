package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;

import java.util.List;
import java.util.Optional;

@Repository
public interface DuelRepo extends JpaRepository<Duel, Long> {

    @EntityGraph(attributePaths = {"heroes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT d FROM Duel d ORDER BY d.createdAt DESC")
    List<Duel> findAllDuelsSortedByDate(@Param("id") Long id);

    List<Duel> findByHeroesIdOrderByCreatedAtDesc(Long heroId);
}
