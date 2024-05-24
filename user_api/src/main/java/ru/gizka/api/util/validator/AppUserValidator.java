package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.service.appUser.AppUserService;

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
        log.info("Проверяем нового пользователя: {}", userDto.getLogin());
        validateCreate(userDto, errors);
    }

    private void validateCreate(RequestAppUserDto userDto, Errors errors) {
        if (appUserService.getByLoginOptional(userDto.getLogin()).isPresent()) {
            errors.rejectValue("login", "", "Логин занят");
            log.info("Логин занят");
        }

        if(userDto.getLogin() == null || userDto.getLogin().equals("null")){
            errors.rejectValue("login", "", "Недопустимый логин");
            log.info("Недопустимый логин");
        }
    }
}
