package ru.gizka.api.repo.old;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gizka.api.model.old.notification.Notification;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findByAppUserLoginOrderByCreatedAtDesc(String userLogin);
}
