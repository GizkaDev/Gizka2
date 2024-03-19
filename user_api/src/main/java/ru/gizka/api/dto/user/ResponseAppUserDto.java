package ru.gizka.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.user.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAppUserDto {

    private Long id;
    private String login;
    private String registeredAt;
    private Set<Role> roles;
}
