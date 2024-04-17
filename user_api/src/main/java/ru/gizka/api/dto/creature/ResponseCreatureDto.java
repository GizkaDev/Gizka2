package ru.gizka.api.dto.creature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.hero.Hero;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCreatureDto {
    private Long id;
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
    private Integer currentHp;
    private Integer currentCon;

    public ResponseCreatureDto(Creature creature) {
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
        this.currentHp = creature.getCurrentHp();
        this.currentCon = creature.getCurrentCon();
    }

    public ResponseCreatureDto(ResponseCreatureDto creatureDto) {
        this.name = creatureDto.getName();
        this.createdAt = creatureDto.getCreatedAt();
        this.race = creatureDto.getRace();

        this.str = creatureDto.getStr();
        this.dex = creatureDto.getDex();
        this.con = creatureDto.getCon();

        this.minInit = creatureDto.getMinInit();
        this.maxInit = creatureDto.getMaxInit();
        this.minAttack = creatureDto.getMinAttack();
        this.maxAttack = creatureDto.getMaxAttack();
        this.minEvasion = creatureDto.getMinEvasion();
        this.maxEvasion = creatureDto.getMaxEvasion();
        this.minPhysDamage = creatureDto.getMinPhysDamage();
        this.maxPhysDamage = creatureDto.getMaxPhysDamage();
        this.maxHp = creatureDto.getMaxHp();
        this.currentHp = creatureDto.getCurrentHp();
        this.currentCon = creatureDto.getCurrentCon();
    }
}
