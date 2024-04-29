package ru.gizka.api.service.actionLogic;

import org.springframework.stereotype.Service;
import ru.gizka.api.model.hero.Hero;

@Service
public class HeroActionLogic {
    public Hero treat(Hero hero) {
        int hpLack = hero.getMaxHp() - hero.getCurrentHp();
        int healAmount = Math.min(hpLack, hero.getWis());
        hero.setCurrentHp(hero.getCurrentHp() + healAmount);
        return hero;
    }
}
