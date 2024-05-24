package ru.gizka.api.facade;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.dto.appUser.ResponseAppUserDto;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.appUser.AppUserService;
import ru.gizka.api.config.security.jwt.JwtService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.AppUserValidator;

import java.util.List;

@Service
@Slf4j
public class AppUserFacade {

    private final AppUserService appUserService;
    private final AppUserValidator appUserValidator;
    private final DtoConverter dtoConverter;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Autowired
    public AppUserFacade(AppUserService appUserService,
                         DtoConverter dtoConverter,
                         JwtService jwtService,
                         AppUserValidator appUserValidator,
                         AuthenticationManager authManager) {
        this.appUserService = appUserService;
        this.dtoConverter = dtoConverter;
        this.jwtService = jwtService;
        this.appUserValidator = appUserValidator;
        this.authManager = authManager;
    }

    public String getToken(RequestAppUserDto userDto) {
        log.info("Начинаем выдачу токена для пользователя: {}", userDto.getLogin());
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getLogin(), userDto.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userDto.getLogin());
        }
        throw new BadCredentialsException("Неверный логин или пароль");
    }

    public ResponseAppUserDto create(RequestAppUserDto userDto, BindingResult bindingResult) {
        log.info("Начинаем создание нового пользователя: {}", userDto.getLogin());
        checkValues(userDto, bindingResult);
        AppUser userToSave = dtoConverter.getModel(userDto);
        AppUser savedUser = appUserService.create(userToSave);
        return dtoConverter.getResponseDto(savedUser);
    }

    public ResponseAppUserDto getByLogin(String login) {
        log.info("Сервис пользователей начинает поиск пользователя: {}", login);
        AppUser user = appUserService.getByLoginOptional(login)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не найден"));
        return dtoConverter.getResponseDto(user);
    }

    public void deleteByLogin(String login) {
        log.info("Сервис пользователей начинает удаление пользователя: {}", login);
        AppUser user = appUserService.getByLoginOptional(login)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не найден"));
        appUserService.delete(user);
    }

    public ResponseAppUserDto verifyAdmin(String login, String secret) {
        log.info("Сервис пользователей начинает верификацию администратора: {}", login);
        if (jwtService.verifyAdmin(secret)) {
            return dtoConverter.getResponseDto(appUserService.addAdminRights(login));
        }
        throw new InternalAuthenticationServiceException("Верификация не пройдена");
    }

    private void checkValues(RequestAppUserDto userDto,
                             BindingResult bindingResult) {
        appUserValidator.validate(userDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
