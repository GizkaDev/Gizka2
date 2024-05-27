package ru.gizka.api.dto.old.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Turn {
    private Integer turnNum;

    private Fighter attacker;
    private Fighter defender;

    private Integer attackerInit;
    private Integer defenderInit;

    private Integer attack;
    private Integer evasion;
    private Integer physDamage;
    private Integer currentHp;
    private Integer def;
    private Integer realDamage;
}
