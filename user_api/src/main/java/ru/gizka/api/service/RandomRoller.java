package ru.gizka.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomRoller {
    private final Random random;

    @Autowired
    public RandomRoller(Random random) {
        this.random = random;
    }

    public Integer rollInitiative(Integer minInitiative, Integer maxInitiative) {
        return random.nextInt(minInitiative, maxInitiative + 1);
    }

    public Integer rollAttack(Integer minAttack, Integer maxAttack) {
        return random.nextInt(minAttack, maxAttack + 1);
    }

    public Integer rollEvasion(Integer minEvasion, Integer maxEvasion) {
        return random.nextInt(minEvasion, maxEvasion + 1);
    }

    public Integer rollPhysDamage(Integer minPhysDamage, Integer maxPhysDamage) {
        return random.nextInt(minPhysDamage, maxPhysDamage + 1);
    }
}
