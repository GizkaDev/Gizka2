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
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.facade.item.ItemPatternFacade;

@RestController
@RequestMapping("/api/admin/item")
@Slf4j
public class AdminItemPatternController {
    private final ItemPatternFacade itemPatternFacade;

    @Autowired
    public AdminItemPatternController(ItemPatternFacade itemPatternFacade){
        this.itemPatternFacade = itemPatternFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseItemDto> create(@Valid @RequestBody RequestItemPatternDto itemDto,
                                                  BindingResult bindingResult) {
        log.info("Контроллер шаблонов предметов администратора принял запрос POST /item: {}", itemDto);
        ResponseItemDto responseItem = itemPatternFacade.create(itemDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseItem);
    }
}
