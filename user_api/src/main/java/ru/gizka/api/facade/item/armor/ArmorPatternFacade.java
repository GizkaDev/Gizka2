package ru.gizka.api.facade.item.armor;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.item.armor.ResponseArmorPatternDto;
import ru.gizka.api.model.item.armor.ArmorPattern;
import ru.gizka.api.service.item.ProductService;
import ru.gizka.api.service.item.armor.ArmorPatternService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.ArmorPatternValidator;

import java.util.List;

@Service
@Slf4j
public class ArmorPatternFacade {
    private final ArmorPatternService armorPatternService;
    private final ProductService productService;
    private final DtoConverter dtoConverter;
    private final ArmorPatternValidator armorPatternValidator;

    @Autowired
    public ArmorPatternFacade(ArmorPatternService armorPatternService,
                              ProductService productService,
                              DtoConverter dtoConverter,
                              ArmorPatternValidator armorPatternValidator) {
        this.armorPatternService = armorPatternService;
        this.productService = productService;
        this.dtoConverter = dtoConverter;
        this.armorPatternValidator = armorPatternValidator;
    }

    public ResponseArmorPatternDto create(RequestArmorPatternDto armorDto, BindingResult bindingResult) {
        log.info("Сервис шаблонов доспехов начинает создание нового шаблона: {}", armorDto.getName());
        checkValues(armorDto, bindingResult);
        ArmorPattern armorPattern = armorPatternService.create(dtoConverter.getModel(armorDto));
        return dtoConverter.getResponseDto(armorPattern);
    }

    private void checkValues(RequestArmorPatternDto armorDto,
                             BindingResult bindingResult) {
        log.info("Сервис шаблонов доспехов начинает проверку валидности нового шаблона: {}", armorDto.getName());
        armorPatternValidator.validate(armorDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
