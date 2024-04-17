package ru.gizka.api.controller.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.facade.FightFacade;
import ru.gizka.api.model.user.AuthUser;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class FightController {
    private final FightFacade fightFacade;

    @Autowired
    public FightController(FightFacade fightFacade) {
        this.fightFacade = fightFacade;
    }

    @PostMapping("/hero/fight")
    public ResponseEntity<FightDto> simulate(@AuthenticationPrincipal AuthUser authUser,
                                             @RequestParam @NotBlank @Size(min = 1, max = 255)
                                             String name) {
        log.info("Контроллер сражений принял запрос POST /hero/fight для пользователя: {}", authUser.login());
        return fightFacade.simulate(authUser.getUser(), name);
    }
}
