package ru.gizka.api.model.old.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.appUser.AppUser;

import java.util.Date;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    @Lob
    private String message;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "app_user_notification", referencedColumnName = "login")
    private AppUser appUser;

    public Notification(String message, Date createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }
}
