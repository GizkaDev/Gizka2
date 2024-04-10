package ru.gizka.api.dto.hero;

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
//    private String race;
}