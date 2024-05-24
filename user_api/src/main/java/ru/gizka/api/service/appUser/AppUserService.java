package ru.gizka.api.service.appUser;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.model.appUser.Role;
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

    public Optional<AppUser> getByLoginOptional(String login) {
        log.info("Ищем пользователя: {}", login);
        return appUserRepo.findByLogin(login);
    }

    public AppUser getByLogin(String login) {
        log.info("Ищем пользователя: {}", login);
        return appUserRepo.findByLogin(login)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public AppUser create(AppUser user) {
        log.info("Создаем нового пользователя: {}", user.getLogin());
        user.getRoles().add(Role.USER);
        log.info("Шифруем пароль");
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
        AppUser appUser = getByLoginOptional(login)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Пользователь %s не найден", login)));
        log.info(String.format("Сервис пользователей наделяет пользователя: %s правами администратора", login));
        appUser.getRoles().add(Role.ADMIN);
        return appUserRepo.save(appUser);
    }

    @Transactional
    public AppUser save(AppUser appUser) {
        log.info("Сервис пользователей сохраняет пользователя: {}", appUser.getLogin());
        return appUserRepo.save(appUser);
    }
}
