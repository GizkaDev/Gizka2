package ru.gizka.api.model.old.item.armor;

public enum ArmorType {
    CLOTHES("Одежда"),
    LIGHT("Лёгкая"),
    MEDIUM("Средняя"),
    HEAVY("Тяжелая");

    private String description;

    ArmorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
