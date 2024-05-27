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
import ru.gizka.api.dto.old.race.RequestRaceDto;
import ru.gizka.api.dto.old.race.ResponseRaceDto;
import ru.gizka.api.facade.old.RaceFacade;

@RestController
@RequestMapping("/api/admin/race")
@Slf4j
public class AdminRaceController {
    private final RaceFacade raceFacade;

    @Autowired
    public AdminRaceController(RaceFacade raceFacade){
        this.raceFacade = raceFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseRaceDto> create(@Valid @RequestBody RequestRaceDto raceDto,
                                                  BindingResult bindingResult){
        log.info("Контроллер рас администратора принял запрос POST /race для расы: {}", raceDto.getName());
        ResponseRaceDto responseRace = raceFacade.create(raceDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRace);
    }
}
