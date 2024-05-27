package ru.gizka.api.repo.old;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.old.item.ItemObject;

import java.util.List;

@Repository
public interface ItemObjectRepo extends JpaRepository<ItemObject, Long> {
    List<ItemObject> findByHero(Hero hero);
}
