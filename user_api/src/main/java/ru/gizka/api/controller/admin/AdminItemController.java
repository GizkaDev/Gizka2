package ru.gizka.api.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.item.RequestItemDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.facade.item.ItemFacade;

@RestController
@RequestMapping("/api/admin/item")
@Slf4j
public class AdminItemController {
    private final ItemFacade itemFacade;

    @Autowired
    public AdminItemController(ItemFacade itemFacade){
        this.itemFacade = itemFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseItemDto> create(@Valid @RequestBody RequestItemDto itemDto,
                                                  BindingResult bindingResult) {
        log.info("Контроллер предметов администратора принял запрос POST /item: {}", itemDto);
        ResponseItemDto responseItem = itemFacade.create(itemDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseItem);
    }
}
