package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.service.AppUserService;
import ru.gizka.api.util.DtoConverter;

@Service
@Slf4j
public class AppUserFacade {

    private final AppUserService appUserService;
    private final DtoConverter dtoConverter;

    @Autowired
    public AppUserFacade(AppUserService appUserService,
                         DtoConverter dtoConverter) {
        this.appUserService = appUserService;
        this.dtoConverter = dtoConverter;
    }

    public ResponseAppUserDto getByLogin(String login) {
        log.info("Сервис пользователя начинает поиск пользователя: {}", login);
        AppUser user = appUserService.getByLogin(login)
                .orElseThrow(()->
                        new EntityNotFoundException("Пользователь не найден"));
        return dtoConverter.getResponseDto(user);
    }

    public void deleteByLogin(String login) {
        log.info("Сервис пользователя начинает удаление пользователя: {}", login);
        AppUser user = appUserService.getByLogin(login)
                .orElseThrow(()->
                        new EntityNotFoundException("Пользователь не найден"));
        appUserService.delete(user);
    }
}
