package ru.gizka.api.dto.notification;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDto {
    private String message;
    private Date createdAt;
}
