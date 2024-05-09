package ru.gizka.api.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.facade.item.ItemObjectFacade;
import ru.gizka.api.model.user.AuthUser;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user/hero/inventory")
public class ItemObjectController {
    private final ItemObjectFacade itemObjectFacade;

    @Autowired
    public ItemObjectController(ItemObjectFacade itemObjectFacade) {
        this.itemObjectFacade = itemObjectFacade;
    }

    @GetMapping()
    public ResponseEntity<List<ResponseItemDto>> getInventory(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Контроллер предметов принял запрос GET /inventory текущего героя для пользователя: {}", authUser.login());
        return ResponseEntity.ok(itemObjectFacade.getInventory(authUser.getUser()));
    }
}
