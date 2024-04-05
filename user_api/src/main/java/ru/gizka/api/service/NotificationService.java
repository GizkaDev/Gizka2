package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.repo.NotificationRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {
    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(NotificationRepo notificationRepo){
        this.notificationRepo = notificationRepo;
    }

    public List<Notification> getAllByLoginSortedByDate(String login){
        log.info("Сервис событий ищет события для пользователя: {}", login);
        return notificationRepo.findByAppUserLoginOrderByCreatedAtDesc(login);
    }

    public Notification save(Notification notification){
        log.info("Сервис событий сохраняет событие: [{}] для пользователя: {}", notification.getMessage(), notification.getAppUser().getLogin());
        return notificationRepo.save(notification);
    }
}
