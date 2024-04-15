package ru.gizka.api.dto.hero;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseHeroDto {
    private Long id;
    private String name;
    private String lastname;
    private Integer str;
    private Integer dex;
    private Integer con;
    private Date createdAt;
    private String userLogin;
    private String status;
    private String race;
    private Integer minInit;
    private Integer maxInit;
    private Integer minAttack;
    private Integer maxAttack;
    private Integer minEvasion;
    private Integer maxEvasion;
    private Integer minPhysDamage;
    private Integer maxPhysDamage;
    private Integer maxHp;
    private Integer currentHp;
}