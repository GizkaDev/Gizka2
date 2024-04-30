package ru.gizka.api.service.actionLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gizka.api.model.hero.Hero;

import java.util.Date;

@Service
@Slf4j
public class HeroActionLogic {
    public Hero treat(Hero hero) {
        int hpLack = hero.getMaxHp() - hero.getCurrentHp();
        int healAmount = Math.min(hpLack, hero.getWis());
        int beforeHeal = hero.getCurrentHp();
        hero.setCurrentHp(hero.getCurrentHp() + healAmount);
        hero.setTreatAt(new Date());
        log.info(String.format("Сервис действий перевязывает раны на %d (было: %s)герою: %s %s(%s)",
                healAmount, beforeHeal, hero.getName(), hero.getLastname(), hero.getAppUser().getLogin()));
        return hero;
    }
}
