package ru.gizka.api.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.model.user.Role;
import ru.gizka.api.repo.AppUserRepo;

import java.util.*;

import static ru.gizka.api.config.security.SecurityConfig.PASSWORD_ENCODER;


@Service
@Transactional(readOnly = true)
@Slf4j
public class AppUserService {
    private final AppUserRepo appUserRepo;

    @Autowired
    public AppUserService(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    public Optional<AppUser> getByLogin(String login) {
        log.info("Сервис пользователей ищет пользователя: {}", login);
        return appUserRepo.findByLogin(login);
    }

    @Transactional
    public AppUser create(AppUser user) {
        log.info("Сервис пользователей создает нового пользователя: {}", user.getLogin());
        Set<Role> roles = new HashSet<>();
        List<Hero> heroes = new ArrayList<>();
        user.setHeroes(heroes);
        user.setRoles(roles);
        user.getRoles().add(Role.USER);
        user.setRegisteredAt(new Date());
        user.setPassword(PASSWORD_ENCODER.encode((user.getPassword())));
        return appUserRepo.save(user);
    }

    @Transactional
    public void delete(AppUser user) {
        log.info("Сервис пользователей удаляет пользователя: {}", user.getLogin());
        appUserRepo.deleteById(user.getId());
    }

    @Transactional
    public AppUser addAdminRights(String login) {
        AppUser appUser = getByLogin(login)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Пользователь %s не найден", login)));
        log.info(String.format("Сервис пользователей наделяет пользователя: %s правами администратора", login));
        appUser.getRoles().add(Role.ADMIN);
        return appUserRepo.save(appUser);
    }

    @Transactional
    public AppUser save(AppUser appUser){
        log.info("Сервис пользователей сохраняет пользователя: {}", appUser.getLogin());
        return appUserRepo.save(appUser);
    }
}
