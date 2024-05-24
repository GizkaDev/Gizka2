package ru.gizka.api.controller.appUser.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.appUser.ResponseAppUserDto;
import ru.gizka.api.facade.AppUserFacade;
import ru.gizka.api.model.appUser.AuthUser;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class AppUserController1 {

    private final AppUserFacade appUserFacade;

    @Autowired
    public AppUserController1(AppUserFacade appUserFacade) {
        this.appUserFacade = appUserFacade;
    }

    @GetMapping("/own")
    public ResponseEntity<ResponseAppUserDto> getOwn(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер пользователей принял запрос GET /own для пользователя: {}", authUser.login());
        return ResponseEntity.ok(appUserFacade.getByLogin(authUser.login()));
    }

    @DeleteMapping("/own")
    public ResponseEntity<HttpStatus> deleteOwn(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер пользователей принял запрос DELETE /own для пользователя: {}", authUser.login());
        appUserFacade.deleteByLogin(authUser.login());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/own")
    public ResponseEntity<ResponseAppUserDto> verifyAdmin(@AuthenticationPrincipal AuthUser authUser,
                                                          @RequestBody String secret){
        log.info("Контроллер пользователей принял запрос PUT /own для пользователя: {}", authUser.login());
        return ResponseEntity.ok(appUserFacade.verifyAdmin(authUser.login(), secret));
    }
}
