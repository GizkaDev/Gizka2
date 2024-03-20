package ru.gizka.api.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.event.EventDto;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.EventService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class EventFacade {
    private final EventService eventService;
    private final DtoConverter dtoConverter;

    @Autowired
    public EventFacade(EventService eventService,
                       DtoConverter dtoConverter) {
        this.eventService = eventService;
        this.dtoConverter = dtoConverter;
    }

    public List<EventDto> getAllEventsByLoginSortedByDate(AppUser appUser) {
        log.info("Сервис событий начинает поиск событий для пользователя: {}", appUser.getLogin());
        return eventService.getAllByLoginSortedByDate(appUser.getLogin()).stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }
}
