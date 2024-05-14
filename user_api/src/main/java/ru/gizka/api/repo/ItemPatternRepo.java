package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.item.ItemPattern;

import java.util.Optional;

@Repository
public interface ItemPatternRepo extends JpaRepository<ItemPattern, Long> {
    Optional<ItemPattern> findByName(String name);

    @Query(value = "SELECT i FROM ItemPattern i ORDER BY RANDOM() LIMIT 1")
    Optional<ItemPattern> findRandom();

    @EntityGraph(attributePaths = {"fights"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT i FROM ItemPattern i WHERE i.name = :name")
    Optional<ItemPattern> findByNameWithFights(String name);
}
