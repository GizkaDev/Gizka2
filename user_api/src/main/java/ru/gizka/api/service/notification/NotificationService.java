package ru.gizka.api.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.repo.NotificationRepo;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public List<Notification> getAllByLoginSortedByDate(String login) {
        log.info("Сервис оповещений ищет события для пользователя: {}", login);
        return notificationRepo.findByAppUserLoginOrderByCreatedAtDesc(login);
    }

    public Notification save(Notification notification, AppUser appUser) {
        log.info("Сервис оповещений сохраняет оповещение: [{}] для пользователя: {}", notification.getMessage(), appUser.getLogin());
        notification.setAppUser(appUser);
        notification.setCreatedAt(new Date());
        return notificationRepo.save(notification);
    }
}
