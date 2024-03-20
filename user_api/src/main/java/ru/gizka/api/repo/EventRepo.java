package ru.gizka.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.event.Event;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    List<Event> findByAppUserLoginOrderByCreatedAtDesc(String userLogin);
}
