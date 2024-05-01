package ru.gizka.api.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.notification.NotificationService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class NotificationFacade {
    private final NotificationService notificationService;
    private final DtoConverter dtoConverter;

    @Autowired
    public NotificationFacade(NotificationService notificationService,
                              DtoConverter dtoConverter) {
        this.notificationService = notificationService;
        this.dtoConverter = dtoConverter;
    }

    public List<NotificationDto> getAllByLoginSortedByDate(AppUser appUser) {
        log.info("Сервис событий начинает поиск событий для пользователя: {}", appUser.getLogin());
        return notificationService.getAllByLoginSortedByDate(appUser.getLogin()).stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }
}
