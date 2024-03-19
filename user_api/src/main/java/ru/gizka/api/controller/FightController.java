package ru.gizka.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.facade.FightFacade;
import ru.gizka.api.model.user.AuthUser;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class FightController {
    private final FightFacade fightFacade;

    @Autowired
    public FightController(FightFacade fightFacade) {
        this.fightFacade = fightFacade;
    }

    @PostMapping("/hero/duel")
    public ResponseEntity<FightDto> simulateDuel(@AuthenticationPrincipal AuthUser authUser,
                                                 @RequestParam String login) {
        log.info("Контроллер сражений принял запрос POST /hero/duel для пользователя: {}", authUser.login());
        return fightFacade.simulateDuel(authUser.getUser(), login);
    }

    @GetMapping("/hero/{id}/fight")
    public ResponseEntity<List<FightDto>> getAllByHeroId(@AuthenticationPrincipal AuthUser authUser,
                                                         @PathVariable Long id) {
        log.info("Контроллер сражений принял запрос GET /hero/{}/fight для пользователя: {}", id, authUser.login());
        return fightFacade.getAllByOwnHeroId(authUser.getUser(), id);
    }
}
