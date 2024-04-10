package ru.gizka.api.controller.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.fight.DuelDto;
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
    public ResponseEntity<DuelDto> simulateDuel(@AuthenticationPrincipal AuthUser authUser,
                                                @RequestParam @NotBlank @Size(max = 255)
                                                String login) {
        log.info("Контроллер сражений принял запрос POST /hero/duel для пользователя: {}", authUser.login());
        return fightFacade.simulateDuel(authUser.getUser(), login);
    }

    @GetMapping("/hero/duel")
    public ResponseEntity<List<DuelDto>> getAllDuelForCurrentHero(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер сражений принял запрос GET /hero/duel для пользователя: {}", authUser.login());
        return fightFacade.getAllDuelsForCurrentHero(authUser.getUser());
    }
}
