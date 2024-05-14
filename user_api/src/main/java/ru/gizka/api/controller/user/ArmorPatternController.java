package ru.gizka.api.controller.user;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.item.armor.ResponseArmorDto;
import ru.gizka.api.facade.item.armor.ArmorPatternFacade;

@RestController
@RequestMapping("/api/armor")
@Slf4j
public class ArmorPatternController {
    private final ArmorPatternFacade armorPatternFacade;

    @Autowired
    public ArmorPatternController(ArmorPatternFacade armorPatternFacade) {
        this.armorPatternFacade = armorPatternFacade;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseArmorDto> getByName(@PathVariable @Size(min = 1, max = 200) String name) {
        log.info("Контроллер шаблонов доспехов принял запрос POST /{}", name);
        return ResponseEntity.ok(armorPatternFacade.getByName(name));
    }
}
