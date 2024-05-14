package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.item.armor.ArmorObject;

@Repository
public interface ArmorObjectRepo extends JpaRepository<ArmorObject, Long> {
}
