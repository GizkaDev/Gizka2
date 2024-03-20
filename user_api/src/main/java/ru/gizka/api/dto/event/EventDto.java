package ru.gizka.api.dto.event;

import lombok.Data;

@Data
public class EventDto {
    private String message;
    private String createdAt;
}
