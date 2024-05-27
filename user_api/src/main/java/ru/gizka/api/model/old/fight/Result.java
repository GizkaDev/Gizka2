package ru.gizka.api.model.old.fight;

public enum Result {
    ATTACKER("Победил атакующий"),
    DEFENDER("Победил защищающийся"),
    DRAW("Ничья");

    private String description;

    Result(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
