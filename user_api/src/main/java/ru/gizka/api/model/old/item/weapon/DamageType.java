package ru.gizka.api.model.old.item.weapon;

public enum DamageType {
    PIERCING("Колющий"),
    SLASHING("Рубящий"),
    BLUDGEONING("Дробящий");

    private String description;

    DamageType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
