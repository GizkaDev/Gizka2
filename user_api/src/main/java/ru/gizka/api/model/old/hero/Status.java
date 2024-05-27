package ru.gizka.api.model.old.hero;

public enum Status {
    ALIVE("Жив"),
    DEAD("Мертв");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
