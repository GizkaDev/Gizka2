package ru.gizka.api.dto.appUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.appUser.Role;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAppUserDto {
    private Long id;
    private String login;
    private Date registeredAt;
    private Set<Role> roles;
}
