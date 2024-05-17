package ru.gizka.api.controller.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.facade.item.armor.ArmorObjectFacade;
import ru.gizka.api.model.user.AuthUser;

@RestController
@Slf4j
@RequestMapping("/api/user/hero/inventory/armor")
public class ArmorObjectController {
    private final ArmorObjectFacade armorObjectFacade;

    @Autowired
    public ArmorObjectController(ArmorObjectFacade armorObjectFacade) {
        this.armorObjectFacade = armorObjectFacade;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseHeroDto> equipArmor(@AuthenticationPrincipal AuthUser authUser,
                                                      @PathVariable @NotNull @Size(min = 1, max = 200) String id) {
        log.info("Контроллер доспехов принял запрос PUT /inventory/armor/{} текущего героя для пользователя: {}", id, authUser.login());
        return ResponseEntity.ok(armorObjectFacade.equipArmor(authUser.getUser(), Long.parseLong(id)));
    }
}
