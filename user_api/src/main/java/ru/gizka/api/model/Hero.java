package ru.gizka.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "hero")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hero {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Size(min = 1, max = 50)
    @NotBlank
    private String name;

    @Column(name = "lastname")
    @Size(min = 1, max = 100)
    @NotBlank
    private String lastname;

    @Column(name = "str")
    @Positive
    private Integer str;

    @Column(name = "dex")
    @Positive
    private Integer dex;

    @Column(name = "con")
    @Positive
    private Integer con;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "app_user_login", referencedColumnName = "login")
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
