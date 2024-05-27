package ru.gizka.api.controller.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.old.fight.DuelDto;
import ru.gizka.api.facade.old.DuelFacade;
import ru.gizka.api.model.appUser.AuthUser;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class DuelController {
    private final DuelFacade duelFacade;

    @Autowired
    public DuelController(DuelFacade duelFacade) {
        this.duelFacade = duelFacade;
    }

    @PostMapping("/hero/duel")
    public ResponseEntity<DuelDto> simulateDuel(@AuthenticationPrincipal AuthUser authUser,
                                                @RequestParam @NotBlank @Size(max = 255)
                                                String login) {
        log.info("Принят запрос POST /api/user/hero/duel от пользователя: {}", authUser.login());
        return duelFacade.simulateDuel(authUser.getUser(), login);
    }

    @GetMapping("/hero/duel")
    public ResponseEntity<List<DuelDto>> getAllDuelForCurrentHero(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Принят запрос GET /api/user/hero/duel от пользователя: {}", authUser.login());
        return duelFacade.getAllDuelsForCurrentHero(authUser.getUser());
    }
}
