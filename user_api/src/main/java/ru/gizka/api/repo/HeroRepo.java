package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.hero.Hero;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeroRepo extends JpaRepository<Hero, Long> {
    @Query("SELECT h FROM Hero h WHERE h.appUser.login = :login AND h.status = 'ALIVE'")
    List<Hero> findAllByLoginAndAlive(@Param("login") String login);

    @EntityGraph(attributePaths = {"fights"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT h FROM Hero h WHERE h.id = :id")
    Optional<Hero> findByIdWithFights(Long id);

    @Query("SELECT COUNT(h) > 0 FROM Hero h WHERE h.id = :heroId AND h.appUser.login = :userLogin")
    boolean isOwner(@Param("heroId") Long heroId, @Param("userLogin") String userLogin);
}
