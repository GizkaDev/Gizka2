package ru.gizka.api.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.notification.Notification;

@Service
@Slf4j
public class NotificationBuilder {

    public Notification buildForFight(Fight fight) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) для сражения",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin());
        String result = "";
        if (fight.getResult().equals(Result.ATTACKER)) {
            result = "и победили";
        } else if (fight.getResult().equals(Result.DEFENDER)) {
            result = "и проиграли";
        } else {
            result = ", и у вас ничья";
        }
        return Notification.builder()
                .message(String.format("Вы встретились в бою с %s %s.",
                        fight.getCreature().getName(), result))
                .build();
    }

    public Notification buildForAttacker(Duel duel) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) для дуэли для атакующего",
                duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin());
        String result = "";
        if (duel.getResult().equals(Result.ATTACKER)) {
            result = "и победили";
        } else if (duel.getResult().equals(Result.DEFENDER)) {
            result = "и проиграли";
        } else {
            result = ", и у вас ничья";
        }
        return Notification.builder()
                .message(String.format("Вы вызвали на дуэль %s %s(%s)%s.",
                        duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin(),
                        result))
                .build();
    }

    public Notification buildForDefender(Duel duel) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) для дуэли для защищающегося",
                duel.getHeroes().get(1).getName(), duel.getHeroes().get(1).getLastname(), duel.getHeroes().get(1).getAppUser().getLogin());
        String result = "";
        if (duel.getResult().equals(Result.DEFENDER)) {
            result = "и вы победили";
        } else if (duel.getResult().equals(Result.ATTACKER)) {
            result = "и вы проиграли";
        } else {
            result = "у вас ничья";
        }
        return Notification.builder()
                .message(String.format("Вас вызвал на дуэль %s %s(%s), %s.",
                        duel.getHeroes().get(0).getName(), duel.getHeroes().get(0).getLastname(), duel.getHeroes().get(0).getAppUser().getLogin(),
                        result))
                .build();
    }

    public Notification buildForDeath(Hero hero) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) при смерти",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return Notification.builder()
                .message(String.format("Ваш герой %s %s погиб", hero.getName(), hero.getLastname()))
                .build();
    }

    public Notification buildForNewHero(Hero hero) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) при новом герое",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return Notification.builder()
                .message(String.format("Еще один авантюрист %s %s готов к приключениям", hero.getName(), hero.getLastname()))
                .build();
    }

    public Notification buildForTreat(Hero hero, int beforeHeal) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) при лечении",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return Notification.builder()
                .message(String.format("Герой %s %s перевязал свои раны на %d ОЗ", hero.getName(), hero.getLastname(), hero.getCurrentHp() - beforeHeal))
                .build();
    }

    public Notification buildForLoot(Fight fight) {
        log.info("Сервис оповещений создает новое оповещение для героя: {} {}({}) для получения добычи",
                fight.getHero().getName(), fight.getHero().getLastname(), fight.getHero().getAppUser().getLogin());
        StringBuilder loots = new StringBuilder();
        for (ItemPattern itemPattern : fight.getLoot()) {
            loots.append(itemPattern.getName()).append(" ");
        }
        return Notification.builder()
                .message(String.format("С поверженного врага вы сняли %s", loots))
                .build();
    }
}
