package ru.gizka.api.dto.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.hero.Hero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fighter {
    private String name;
    private String lastname;
    private String createdAt;
    private String userLogin;
    private String status;

    private Integer str;
    private Integer dex;
    private Integer con;

    private Integer attack;
    private Integer evasion;
    private Integer physDamage;
    private Integer maxHp;
    private Integer initiative;
    private Integer currentHp;

    public Fighter(Fighter fighter){
        this.name = fighter.getName();
        this.lastname = fighter.getLastname();
        this.createdAt = fighter.getCreatedAt();
        this.userLogin = fighter.getUserLogin();
        this.status = fighter.getStatus();
        this.str = fighter.getStr();
        this.dex = fighter.getDex();
        this.con = fighter.getCon();
        this.attack = fighter.getAttack();
        this.evasion = fighter.getEvasion();
        this.physDamage = fighter.getPhysDamage();
        this.maxHp = fighter.getMaxHp();
        this.initiative = fighter.getInitiative();
        this.currentHp = fighter.getCurrentHp();
    }
}