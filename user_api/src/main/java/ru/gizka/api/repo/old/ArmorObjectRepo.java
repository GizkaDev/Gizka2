package ru.gizka.api.repo.old;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.old.item.armor.ArmorObject;

@Repository
public interface ArmorObjectRepo extends JpaRepository<ArmorObject, Long> {
}
