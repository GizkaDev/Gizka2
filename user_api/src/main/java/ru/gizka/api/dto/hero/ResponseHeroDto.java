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
    private Long currentWeight;
    private Long maxWeight;
}