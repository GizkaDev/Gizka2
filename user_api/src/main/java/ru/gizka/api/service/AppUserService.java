package ru.gizka.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.model.Role;
import ru.gizka.api.repo.AppUserRepo;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ru.gizka.api.config.security.SecurityConfig.PASSWORD_ENCODER;


@Service
@Transactional(readOnly = true)
public class AppUserService {

    private final AppUserRepo appUserRepo;

    @Autowired
    public AppUserService(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    public Optional<AppUser> getByLogin(String login) {
        return appUserRepo.findByLogin(login);
    }

    @Transactional
    public AppUser create(AppUser user) {
        Set<Role> roles = new HashSet<>();
        user.setRoles(roles);
        user.getRoles().add(Role.USER);
        user.setRegisteredAt(new Date());
        user.setPassword(PASSWORD_ENCODER.encode((user.getPassword())));
        return appUserRepo.save(user);
    }
}
