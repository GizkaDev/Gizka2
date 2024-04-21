package ru.gizka.api.service;

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

    public void saveNotification(Fight fight, AppUser appUser) {
        saveNotificationForUser(fight, appUser);
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({})",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin());
    }

    public void saveNotification(Duel duel) {
        saveNotificationForAttacker(duel);
        saveNotificationForDefender(duel);
        log.info("Сервис оповещений создает новое оповещение для героев: {} {}({}) и {} {}({})",
                duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin(),
                duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin());
    }


    private void saveNotificationForUser(Fight fight, AppUser appUser) {
        String result = "";
        if (fight.getResult().equals(Result.ATTACKER)) {
            result = "и победили";
        } else if (fight.getResult().equals(Result.DEFENDER)) {
            result = "и проиграли";
        } else {
            result = ", и у вас ничья";
        }
        Notification notification = Notification.builder()
                .message(String.format("Вы встретились в бою с %s %s.",
                        fight.getCreature().getName(), result))
                .build();
        save(notification, appUser);
    }

    private void saveNotificationForAttacker(Duel duel) {
        String result = "";
        if (duel.getResult().equals(Result.ATTACKER)) {
            result = "и победили";
        } else if (duel.getResult().equals(Result.DEFENDER)) {
            result = "и проиграли";
        } else {
            result = ", и у вас ничья";
        }
        Notification notification = Notification.builder()
                .message(String.format("Вы вызвали на дуэль %s %s(%s)%s.",
                        duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin(),
                        result))
                .build();
        save(notification, duel.getHeroes().get(0).getAppUser());
    }

    private void saveNotificationForDefender(Duel duel) {
        String result = "";
        if (duel.getResult().equals(Result.DEFENDER)) {
            result = "и вы победили";
        } else if (duel.getResult().equals(Result.ATTACKER)) {
            result = "и вы проиграли";
        } else {
            result = "у вас ничья";
        }
        Notification notification = Notification.builder()
                .message(String.format("Вас вызвал на дуэль %s %s(%s), %s.",
                        duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin(),
                        result))
                .build();
       save(notification, duel.getHeroes().get(1).getAppUser());
    }
}
