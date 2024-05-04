package ru.gizka.api.controller.user;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.facade.item.ItemFacade;

@RestController
@RequestMapping("/api/item")
@Slf4j
public class ItemController {
    private final ItemFacade itemFacade;

    @Autowired
    public ItemController(ItemFacade itemFacade){
        this.itemFacade = itemFacade;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseItemDto> getByName(@PathVariable @Size(min = 1, max = 200) String name) {
        log.info("Контроллер предметов принял запрос POST /{name}");
        return ResponseEntity.ok(itemFacade.getByName(name));
    }
}
