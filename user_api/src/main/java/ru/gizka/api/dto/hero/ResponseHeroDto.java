package ru.gizka.api.dto.hero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.model.hero.Hero;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseHeroDto {
    private Long id;
    private String name;
    private String lastname;
    private Date createdAt;
    private String userLogin;
    private String status;
    private String race;
    private Date treatAt;

    private Integer str;
    private Integer dex;
    private Integer con;
    private Integer wis;

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

    public ResponseHeroDto(Hero hero) {
        this.name = hero.getName();
        this.lastname = hero.getLastname();
        this.createdAt = hero.getCreatedAt();
        this.userLogin = hero.getAppUser().getLogin();
        this.status = hero.getStatus().name();
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
        this.currentHp = hero.getCurrentHp();
        this.currentCon = hero.getCurrentCon();
    }
}