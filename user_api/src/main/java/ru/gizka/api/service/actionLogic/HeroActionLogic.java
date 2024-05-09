package ru.gizka.api.service.actionLogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.service.fightLogic.FightLogic;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HeroActionLogic {
    private final ObjectMapper objectMapper;
    private final FightLogic fightLogic;

    @Autowired
    public HeroActionLogic(ObjectMapper objectMapper,
                           FightLogic fightLogic) {
        this.objectMapper = objectMapper;
        this.fightLogic = fightLogic;
    }

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

    public Fight simulateFight(Hero hero, Creature creature) {
        log.info("Сервис логики сражения готовит компоненты для героя: {} {}({}) и моба: {}",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin(),
                creature.getName());
        Fighter fighter1 = new Fighter(hero);
        List<Turn> turns = fightLogic.simulate(fighter1, new Fighter(creature));
        String turnsAsString = "";
        try {
            turnsAsString = objectMapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (turns.get(turns.size() - 1).getAttacker().getName().equals(fighter1.getName())) {
            hero.setCurrentHp(turns.get(turns.size() - 1).getAttacker().getCurrentHp());
        } else {
            hero.setCurrentHp(turns.get(turns.size() - 1).getDefender().getCurrentHp());
        }
        return Fight.builder()
                .hero(hero)
                .creature(creature)
                .turns(turnsAsString)
                .result(getResult(turns, fighter1.getName()))
                .createdAt(new Date())
                .build();
    }

    public Duel simulateDuel(Hero hero1, Hero hero2) {
        log.info("Сервис логики сражений готовит компоненты для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        Fighter fighter1 = new Fighter(hero1);
        List<Turn> turns = fightLogic.simulate(fighter1, new Fighter(hero2));
        String turnsAsString = "";
        try {
            turnsAsString = objectMapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Duel.builder()
                .heroes(List.of(hero1, hero2))
                .turns(turnsAsString)
                .result(getResult(turns, fighter1.getName()))
                .createdAt(new Date())
                .build();
    }

    //Если хп ни у кого не опустилось до нуля и ниже, то ничья
    //Если у кого-то ниже, то побеждает всегда последний атакующий, т.к.
    //он последний наносит урон
    //Логин последнего атакующего == логину первого героя, то он победитель
    //Иначе второй герой
    private Result getResult(List<Turn> turns, String fighter1Name) {
        Fighter lastTurnAttacker = turns.get(turns.size() - 1).getAttacker();
        Fighter lastTurnDefender = turns.get(turns.size() - 1).getDefender();
        Result result;
        if (lastTurnAttacker.getCurrentHp() > 0 && lastTurnDefender.getCurrentHp() > 0) {
            result = Result.DRAW;
            log.info("Сервис логики сражений определил итог: {}", result.getDescription());
        } else if (lastTurnAttacker.getName().equals(fighter1Name)) {
            result = Result.ATTACKER;
            log.info("Сервис логики сражений определил итог: {} {}", result.getDescription(), lastTurnAttacker.getName());
        } else {
            result = Result.DEFENDER;
            log.info("Сервис логики сражений определил итог: {} {}", result.getDescription(), lastTurnAttacker.getName());
        }
        return result;
    }
}
