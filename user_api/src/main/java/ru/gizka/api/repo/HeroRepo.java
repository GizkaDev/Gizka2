package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.Hero;

import java.util.List;

@Repository
public interface HeroRepo extends JpaRepository<Hero, Long> {
    @Query("SELECT h FROM Hero h WHERE h.appUser.login = :login AND h.status = 'ALIVE'")
    List<Hero> findAllByLoginAndAlive(@Param("login") String tgId);
}
