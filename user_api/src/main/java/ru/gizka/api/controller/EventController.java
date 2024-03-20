package ru.gizka.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.event.EventDto;
import ru.gizka.api.facade.EventFacade;
import ru.gizka.api.model.user.AuthUser;

import java.util.List;

@RestController
@RequestMapping("/api/user/event")
@Slf4j
public class EventController {
    private final EventFacade eventFacade;

    @Autowired
    public EventController(EventFacade eventFacade) {
        this.eventFacade = eventFacade;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsSortedByDate(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер событий принял запрос GET /event для пользователя: {}", authUser.login());
        return ResponseEntity.ok(eventFacade.getAllEventsByLoginSortedByDate(authUser.getUser()));
    }
}
