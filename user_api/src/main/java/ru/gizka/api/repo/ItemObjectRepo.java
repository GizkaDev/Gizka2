package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;

import java.util.List;

@Repository
public interface ItemObjectRepo extends JpaRepository<ItemObject, Long> {
    List<ItemObject> findByHero(Hero hero);
}
