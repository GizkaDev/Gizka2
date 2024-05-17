package ru.gizka.api.dto.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.armor.ArmorObject;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fighter {
    private String name;
    private Date createdAt;
    private String race;

    private Integer str;
    private Integer dex;
    private Integer con;

    private Integer minInit;
    private Integer maxInit;
    private Integer minAttack;
    private Integer maxAttack;
    private Integer minEvasion;
    private Integer maxEvasion;
    private Integer minPhysDamage;
    private Integer maxPhysDamage;
    private Integer maxHp;
    private Integer endurance;

    private Integer currentHp;
    private Long currentWeight;
    private Integer def;

    public Fighter(Creature creature) {
        this.name = creature.getName();
        this.createdAt = creature.getCreatedAt();
        this.race = creature.getRace().getName();

        this.str = creature.getStr();
        this.dex = creature.getDex();
        this.con = creature.getCon();

        this.minInit = creature.getMinInit();
        this.maxInit = creature.getMaxInit();
        this.minAttack = creature.getMinAttack();
        this.maxAttack = creature.getMaxAttack();
        this.minEvasion = creature.getMinEvasion();
        this.maxEvasion = creature.getMaxEvasion();
        this.minPhysDamage = creature.getMinPhysDamage();
        this.maxPhysDamage = creature.getMaxPhysDamage();
        this.maxHp = creature.getMaxHp();
        this.endurance = creature.getEndurance();

        this.currentHp = creature.getCurrentHp();
        this.currentWeight = 0L;
        this.def = creature.getDef();
    }

    public Fighter(Hero hero) {
        this.name = String.format("%s %s(%s)", hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        this.createdAt = hero.getCreatedAt();
        this.race = hero.getRace().getName();

        this.str = hero.getStr();
        this.dex = hero.getDex();
        this.con = hero.getCon();

        this.minInit = hero.getMinInit();
        this.maxInit = hero.getMaxInit();
        this.minAttack = hero.getMinAttack();
        this.maxAttack = hero.getMaxAttack();
        this.minEvasion = hero.getMinEvasion();
        this.maxEvasion = hero.getMaxEvasion();
        this.minPhysDamage = hero.getMinPhysDamage();
        this.maxPhysDamage = hero.getMaxPhysDamage();
        this.maxHp = hero.getMaxHp();
        this.endurance = hero.getEndurance();

        this.currentHp = hero.getCurrentHp();
        this.currentWeight = hero.getCurrentWeight();
        this.def = hero.getDef() + (hero.getEquippedArmor() == null ? 0 : hero.getEquippedArmor().getArmor());
    }

    public Fighter(Fighter fighter) {
        this.name = fighter.getName();
        this.createdAt = fighter.getCreatedAt();
        this.race = fighter.getRace();

        this.str = fighter.getStr();
        this.dex = fighter.getDex();
        this.con = fighter.getCon();

        this.minInit = fighter.getMinInit();
        this.maxInit = fighter.getMaxInit();
        this.minAttack = fighter.getMinAttack();
        this.maxAttack = fighter.getMaxAttack();
        this.minEvasion = fighter.getMinEvasion();
        this.maxEvasion = fighter.getMaxEvasion();
        this.minPhysDamage = fighter.getMinPhysDamage();
        this.maxPhysDamage = fighter.getMaxPhysDamage();
        this.maxHp = fighter.getMaxHp();
        this.endurance = fighter.getEndurance();

        this.currentHp = fighter.getCurrentHp();
        this.currentWeight = fighter.getCurrentWeight();
        this.def = fighter.getDef();
    }
}
