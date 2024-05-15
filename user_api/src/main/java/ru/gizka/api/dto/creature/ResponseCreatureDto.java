package ru.gizka.api.dto.creature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.creature.Creature;

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
    private Integer endurance;

    private Integer currentHp;
    private Integer def;
}
