package ru.gizka.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.service.AppUserService;

@Component
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
        validateCreate(userDto, errors);
    }

    private void validateCreate(RequestAppUserDto userDto, Errors errors) {
        if (appUserService.getByLogin(userDto.getLogin()).isPresent()) {
            errors.rejectValue("login", "", "Логин занят");
        }
    }
}
