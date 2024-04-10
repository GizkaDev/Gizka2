package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.user.ResponseAppUserDto;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.AppUserService;
import ru.gizka.api.service.JwtService;
import ru.gizka.api.util.DtoConverter;

@Service
@Slf4j
public class AppUserFacade {

    private final AppUserService appUserService;
    private final DtoConverter dtoConverter;
    private final JwtService jwtService;

    @Autowired
    public AppUserFacade(AppUserService appUserService,
                         DtoConverter dtoConverter,
                         JwtService jwtService) {
        this.appUserService = appUserService;
        this.dtoConverter = dtoConverter;
        this.jwtService = jwtService;
    }

    public ResponseAppUserDto getByLogin(String login) {
        log.info("Сервис пользователей начинает поиск пользователя: {}", login);
        AppUser user = appUserService.getByLogin(login)
                .orElseThrow(()->
                        new EntityNotFoundException("Пользователь не найден"));
        return dtoConverter.getResponseDto(user);
    }

    public void deleteByLogin(String login) {
        log.info("Сервис пользователей начинает удаление пользователя: {}", login);
        AppUser user = appUserService.getByLogin(login)
                .orElseThrow(()->
                        new EntityNotFoundException("Пользователь не найден"));
        appUserService.delete(user);
    }

    public ResponseAppUserDto verifyAdmin(String login, String secret) {
        log.info("Сервис пользователей начинает верификацию администратора: {}", login);
        if(jwtService.verifyAdmin(secret)){
            return dtoConverter.getResponseDto(appUserService.addAdminRights(login));
        }
        throw new InternalAuthenticationServiceException("Верификация не пройдена");
    }
}
