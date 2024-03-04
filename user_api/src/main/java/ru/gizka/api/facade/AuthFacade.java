package ru.gizka.api.facade;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.service.AppUserService;
import ru.gizka.api.service.JwtService;
import ru.gizka.api.util.AppUserValidator;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class AuthFacade {

    private final AppUserService appUserService;
    private final AppUserValidator appUserValidator;
    private final DtoConverter dtoConverter;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public AuthFacade(AppUserService appUserService,
                      AppUserValidator appUserValidator,
                      DtoConverter dtoConverter,
                      AuthenticationManager authManager,
                      JwtService jwtService) {
        this.appUserService = appUserService;
        this.appUserValidator = appUserValidator;
        this.dtoConverter = dtoConverter;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public ResponseAppUserDto create(RequestAppUserDto userDto, BindingResult bindingResult) {
        log.info("Сервис аутентификации начинает создание нового пользователя: {}", userDto.getLogin());
        checkValues(userDto, bindingResult);
        AppUser userToSave = dtoConverter.getModel(userDto);
        AppUser savedUser = appUserService.create(userToSave);
        return dtoConverter.getResponseDto(savedUser);
    }

    public String getToken(RequestAppUserDto userDto) {
        log.info("Сервис аутентификации начинает выдачу токена для {}", userDto.getLogin());
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getLogin(), userDto.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userDto.getLogin());
        }
        throw new BadCredentialsException("Неверный логин или пароль");
    }

    private void checkValues(RequestAppUserDto userDto,
                             BindingResult bindingResult) {
        log.info("Сервис аутентификации начинает проверку валидности нового пользователя: {}", userDto.getLogin());
        appUserValidator.validate(userDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
