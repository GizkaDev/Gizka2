package ru.gizka.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.service.AppUserService;

@Component
@Slf4j
public class AppUserValidator implements Validator {

    private final AppUserService appUserService;

    @Autowired
    public AppUserValidator(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestAppUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestAppUserDto userDto = (RequestAppUserDto) target;
        log.info("Валидатор пользователей проверяет нового пользователя: {}", userDto.getLogin());
        validateCreate(userDto, errors);
    }

    private void validateCreate(RequestAppUserDto userDto, Errors errors) {
        if (appUserService.getByLogin(userDto.getLogin()).isPresent()) {
            errors.rejectValue("login", "", "Логин занят");
            log.info("Валидатор пользователей сообщает, что логин занят: {}", userDto.getLogin());
        }
    }
}
