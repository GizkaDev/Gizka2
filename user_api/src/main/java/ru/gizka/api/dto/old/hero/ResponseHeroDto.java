package ru.gizka.api.dto.old.hero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.old.item.armor.ResponseArmorDto;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHeroDto {
    private Long id;
    private String name;
    private String lastname;

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
    private Integer endurance;
    private Long maxWeight;
    private Integer search;
    private Integer treat;

    private Integer currentHp;
    private Long currentWeight;
    private Integer def;

    private ResponseArmorDto equippedArmor;

    private Date createdAt;
    private String userLogin;
    private String status;
    private String race;
    private Date treatAt;
}