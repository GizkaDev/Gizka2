package ru.gizka.api.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.facade.NotificationFacade;
import ru.gizka.api.model.user.AuthUser;

import java.util.List;

@RestController
@RequestMapping("/api/user/event")
@Slf4j
public class NotificationController {
    private final NotificationFacade notificationFacade;

    @Autowired
    public NotificationController(NotificationFacade notificationFacade) {
        this.notificationFacade = notificationFacade;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllSortedByDate(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер событий принял запрос GET /event для пользователя: {}", authUser.login());
        return ResponseEntity.ok(notificationFacade.getAllByLoginSortedByDate(authUser.getUser()));
    }
}
