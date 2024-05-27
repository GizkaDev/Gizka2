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
import ru.gizka.api.dto.old.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.old.item.armor.ResponseArmorDto;
import ru.gizka.api.facade.old.item.armor.ArmorPatternFacade;

@RestController
@RequestMapping("/api/admin/armor")
@Slf4j
public class AdminArmorPatternController {
    private final ArmorPatternFacade armorPatternFacade;

    @Autowired
    public AdminArmorPatternController(ArmorPatternFacade armorPatternFacade) {
        this.armorPatternFacade = armorPatternFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseArmorDto> create(@Valid @RequestBody RequestArmorPatternDto armorDto,
                                                   BindingResult bindingResult) {
        log.info("Контроллер шаблонов доспехов администратора принял запрос POST /armor: {}", armorDto);
        ResponseArmorDto responseArmorDto = armorPatternFacade.create(armorDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseArmorDto);
    }
}
