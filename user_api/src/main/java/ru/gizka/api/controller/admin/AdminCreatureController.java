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
import ru.gizka.api.dto.old.creature.RequestCreatureDto;
import ru.gizka.api.dto.old.creature.ResponseCreatureDto;
import ru.gizka.api.facade.old.CreatureFacade;

@RestController
@RequestMapping("/api/admin/creature")
@Slf4j
public class AdminCreatureController {
    private final CreatureFacade creatureFacade;

    @Autowired
    public AdminCreatureController(CreatureFacade creatureFacade) {
        this.creatureFacade = creatureFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseCreatureDto> create(@Valid @RequestBody RequestCreatureDto creatureDto,
                                                      BindingResult bindingResult) {
        log.info("Контроллер мобов администратора принял запрос POST /creature для моба: {}", creatureDto.getName());
        ResponseCreatureDto responseCreature = creatureFacade.create(creatureDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreature);
    }
}
