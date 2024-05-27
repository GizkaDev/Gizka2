package ru.gizka.api.controller.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.old.fight.FightDto;
import ru.gizka.api.facade.old.FightFacade;
import ru.gizka.api.model.appUser.AuthUser;

@RestController
@RequestMapping("/api/user/hero/fight")
@Slf4j
public class FightController {
    private final FightFacade fightFacade;

    @Autowired
    public FightController(FightFacade fightFacade) {
        this.fightFacade = fightFacade;
    }

    @PostMapping()
    public ResponseEntity<FightDto> simulate(@AuthenticationPrincipal AuthUser authUser,
                                             @RequestParam @NotBlank @Size(min = 1, max = 255)
                                             String name) {
        log.info("Принят запрос POST /api/user/hero/fight от пользователя: {}", authUser.login());
        return fightFacade.simulate(authUser.getUser(), name);
    }

    @GetMapping()
    public ResponseEntity<FightDto> getLatestForCurrentHero(@AuthenticationPrincipal AuthUser authUser){
        log.info("Принят запрос GET /api/user/hero/fight от пользователя: {}", authUser.login());
        return fightFacade.getLatestByAppUserForCurrentHero(authUser.getUser());
    }
}
