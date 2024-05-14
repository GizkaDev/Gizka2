package ru.gizka.api.model.race;

public enum RaceSize {
    TINY("Крошечный"),
    LITTLE("Маленький"),
    SMALL("Небольшой"),
    AVERAGE("Средний"),
    BIG("Крупный"),
    GREAT("Большой"),
    GIANT("Гигантский");

    private String description;

    RaceSize(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
