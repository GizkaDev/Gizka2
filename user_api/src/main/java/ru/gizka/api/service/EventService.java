package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.event.Event;
import ru.gizka.api.repo.EventRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventService {
    private final EventRepo eventRepo;

    @Autowired
    public EventService(EventRepo eventRepo){
        this.eventRepo = eventRepo;
    }

    public List<Event> getAllByLoginSortedByDate(String login){
        log.info("Сервис событий ищет события для пользователя: {}", login);
        return eventRepo.findByAppUserLoginOrderByCreatedAtDesc(login);
    }

    public Event save(Event event){
        log.info("Сервис событий сохраняет событие: [{}] для пользователя: {}", event.getMessage(), event.getAppUser().getLogin());
        return eventRepo.save(event);
    }
}
