package ru.gizka.api.service.old.actionLogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.old.fight.Fighter;
import ru.gizka.api.dto.old.fight.Turn;
import ru.gizka.api.model.old.creature.Creature;
import ru.gizka.api.model.old.fight.Duel;
import ru.gizka.api.model.old.fight.Fight;
import ru.gizka.api.model.old.fight.Result;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.old.item.ItemObject;
import ru.gizka.api.model.old.item.armor.ArmorObject;
import ru.gizka.api.service.old.fightLogic.FightLogic;
import ru.gizka.api.service.old.item.ItemObjectService;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HeroActionLogic {
    private final ObjectMapper objectMapper;
    private final FightLogic fightLogic;
    private final ItemObjectService itemObjectService;

    @Autowired
    public HeroActionLogic(ObjectMapper objectMapper,
                           FightLogic fightLogic,
                           ItemObjectService itemObjectService) {
        this.objectMapper = objectMapper;
        this.fightLogic = fightLogic;
        this.itemObjectService = itemObjectService;
    }

    @Transactional
    public Hero equipArmor(Hero hero, ArmorObject armorObject) {
        if (hero.getEquippedArmor() != null) {
            log.info("Сервис действий возвращает доспех id: {} в инвентарь героя", hero.getEquippedArmor().getId());
            ArmorObject equippedArmor = hero.getEquippedArmor();
            equippedArmor.setHero(hero);
            equippedArmor.setCarrierId(null);
            hero.getInventory().add(equippedArmor);
        }

        log.info("Сервис действий надевает доспех id: {}", armorObject.getId());
        armorObject.setHero(null);
        armorObject.setCarrierId(hero.getId());
        hero.getInventory().remove(armorObject);
        hero.setEquippedArmor(armorObject);
        return hero;
    }

    @Transactional
    public Hero takeOffArmor(Hero hero) {
        if (hero.getEquippedArmor() != null) {
            log.info("Сервис действий возвращает доспех id: {} в инвентарь героя", hero.getEquippedArmor().getId());
            ArmorObject equippedArmor = hero.getEquippedArmor();
            equippedArmor.setHero(hero);
            equippedArmor.setCarrierId(null);
            hero.getInventory().add(equippedArmor);
        }
        hero.setEquippedArmor(null);
        return hero;
    }

    @Transactional
    public void dropItem(Hero hero, ItemObject itemObject) {
        log.info("Сервис действий выбрасывает предмет id: {}", itemObject);
        hero.setCurrentWeight(hero.getCurrentWeight() - itemObject.getWeight());
        itemObjectService.delete(itemObject);
    }

    public Hero treat(Hero hero) {
        if (isOverweight(hero)) {
            throw new IllegalArgumentException(String.format("Герой перегружен и не может быть вылечен. Текущий вес: %s, максимальный вес: %s",
                    hero.getCurrentWeight(), hero.getMaxWeight()));
        }
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
        if (isOverweight(hero)) {
            throw new IllegalArgumentException(String.format("Герой перегружен и не может сражаться. Текущий вес: %s, максимальный вес: %s",
                    hero.getCurrentWeight(), hero.getMaxWeight()));
        }
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
        return new Fight(
                hero,
                creature,
                turnsAsString,
                getResult(turns, fighter1.getName()),
                new Date());
    }

    public Duel simulateDuel(Hero hero1, Hero hero2) {
        log.info("Сервис логики сражений готовит компоненты для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        if (isOverweight(hero1)) {
            throw new IllegalArgumentException(String.format("Ваш герой перегружен и не может сражаться. Текущий вес: %s, максимальный вес: %s",
                    hero1.getCurrentWeight(), hero1.getMaxWeight()));
        }
        Fighter fighter1 = new Fighter(hero1);
        List<Turn> turns = fightLogic.simulate(fighter1, new Fighter(hero2));
        String turnsAsString = "";
        try {
            turnsAsString = objectMapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Duel(
                List.of(hero1, hero2),
                turnsAsString,
                getResult(turns, fighter1.getName()),
                new Date());
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

    private Boolean isOverweight(Hero hero) {
        return hero.getCurrentWeight() > hero.getMaxWeight();
    }
}
