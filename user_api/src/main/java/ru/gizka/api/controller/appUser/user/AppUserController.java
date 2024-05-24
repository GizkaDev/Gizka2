package ru.gizka.api.controller.appUser.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.dto.appUser.ResponseAppUserDto;
import ru.gizka.api.facade.AppUserFacade;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class AppUserController {

    private final AppUserFacade appUserFacade;

    @Autowired
    public AppUserController (AppUserFacade appUserFacade){
        this.appUserFacade = appUserFacade;
    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseAppUserDto> create(@Valid @RequestBody RequestAppUserDto userDto,
                                                     BindingResult bindingResult)  {
        log.info("Принят запрос POST /api/user/registration");
        ResponseAppUserDto responseUser = appUserFacade.create(userDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody RequestAppUserDto userDto) {
        log.info("Контроллер аутентификации принял запрос POST /token для пользователя: {}", userDto.getLogin());
        return ResponseEntity.ok(appUserFacade.getToken(userDto));
    }
}
