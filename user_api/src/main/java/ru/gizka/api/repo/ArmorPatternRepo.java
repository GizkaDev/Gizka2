package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.item.armor.ArmorPattern;

import java.util.Optional;

@Repository
public interface ArmorPatternRepo extends JpaRepository<ArmorPattern, Long> {
    Optional<ArmorPattern> findByName(String name);
}
