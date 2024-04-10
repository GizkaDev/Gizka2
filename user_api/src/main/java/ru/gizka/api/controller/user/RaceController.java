package ru.gizka.api.controller.user;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.facade.race.RaceFacade;

import java.util.List;

@RestController
@RequestMapping("/api/race")
@Slf4j
public class RaceController {
    private final RaceFacade raceFacade;

    @Autowired
    public RaceController(RaceFacade raceFacade) {
        this.raceFacade = raceFacade;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseRaceDto> getByName(@PathVariable @Size(max = 100) String name) {
        log.info("Контроллер рас принял запрос GET /{name}");
        return ResponseEntity.ok(raceFacade.getByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseRaceDto>> getAll() {
        log.info("Контроллер рас принял запрос GET /all");
        return ResponseEntity.ok(raceFacade.getAll());
    }
}
