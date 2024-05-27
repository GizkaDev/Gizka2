package ru.gizka.api.controller.appUser.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.dto.appUser.ResponseAppUserDto;
import ru.gizka.api.facade.appUser.AppUserFacade;
import ru.gizka.api.model.appUser.AuthUser;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class AppUserController {

    private final AppUserFacade appUserFacade;

    @Autowired
    public AppUserController(AppUserFacade appUserFacade) {
        this.appUserFacade = appUserFacade;
    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseAppUserDto> create(@Valid @RequestBody RequestAppUserDto userDto,
                                                     BindingResult bindingResult) {
        log.info("Принят запрос POST /api/user/registration");
        ResponseAppUserDto responseUser = appUserFacade.create(userDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody RequestAppUserDto userDto) {
        log.info("Принят запрос POST /api/user/token от пользователя: {}", userDto.getLogin());
        return ResponseEntity.ok(appUserFacade.getToken(userDto));
    }

    @GetMapping("/own")
    public ResponseEntity<ResponseAppUserDto> getOwn(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Принят запрос GET /api/user/own от пользователя: {}", authUser.login());
        return ResponseEntity.ok(appUserFacade.getByLogin(authUser.login()));
    }

    @DeleteMapping("/own")
    public ResponseEntity<HttpStatus> deleteOwn(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Принят запрос DELETE /api/user/own от пользователя: {}", authUser.login());
        appUserFacade.deleteByLogin(authUser.login());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/own")
    public ResponseEntity<ResponseAppUserDto> verifyAdmin(@AuthenticationPrincipal AuthUser authUser,
                                                          @RequestBody String secret) {
        log.info("Принят запрос PATCH /api/user/own от пользователя: {}", authUser.login());
        return ResponseEntity.ok(appUserFacade.verifyAdmin(authUser.login(), secret));
    }
}
