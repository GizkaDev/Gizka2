package ru.gizka.api.controller.user;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.creature.ResponseCreatureDto;
import ru.gizka.api.facade.CreatureFacade;

import java.util.List;

@RestController
@RequestMapping("/api/creature")
@Slf4j
public class CreatureController {
    private final CreatureFacade creatureFacade;

    @Autowired
    public CreatureController(CreatureFacade creatureFacade){
        this.creatureFacade = creatureFacade;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseCreatureDto> getByName(@PathVariable @Size(min = 1, max = 100) String name) {
        log.info("Контроллер мобов принял запрос GET /{name}");
        return ResponseEntity.ok(creatureFacade.getByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseCreatureDto>> getAll() {
        log.info("Контроллер мобов принял запрос GET /all");
        return ResponseEntity.ok(creatureFacade.getAll());
    }
}
