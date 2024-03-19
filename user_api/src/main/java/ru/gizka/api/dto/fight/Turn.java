package ru.gizka.api.dto.fight;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.fight.Fight;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
