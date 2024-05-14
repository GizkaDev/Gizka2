package ru.gizka.api.facade.item.armor;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.item.armor.ResponseArmorDto;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.model.item.armor.ArmorPattern;
import ru.gizka.api.service.item.ProductService;
import ru.gizka.api.service.item.armor.ArmorPatternService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.ArmorPatternValidator;

import java.util.List;
import java.util.Optional;

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

    public ResponseArmorDto create(RequestArmorPatternDto armorDto, BindingResult bindingResult) {
        log.info("Сервис шаблонов доспехов начинает создание нового шаблона: {}", armorDto.getName());
        Optional<Product> mbProduct = productService.getByName("Доспехи");
        checkValues(armorDto, bindingResult, mbProduct);
        ArmorPattern armorPattern = dtoConverter.getModel(armorDto);
        armorPattern.setProduct(mbProduct.get());
        ArmorPattern saved = armorPatternService.create(armorPattern);
        return dtoConverter.getResponseDto(saved);
    }

    public ResponseArmorDto getByName(String name) {
        log.info("Сервис шаблонов доспехов начинает поиск шаблона: {}", name);
        ArmorPattern armorPattern = armorPatternService.getByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Шаблон не найден: %s", name)));
        return dtoConverter.getResponseDto(armorPattern);
    }

    private void checkValues(RequestArmorPatternDto armorDto,
                             BindingResult bindingResult,
                             Optional<Product> mbProduct) {
        log.info("Сервис шаблонов доспехов начинает проверку валидности нового шаблона: {}", armorDto.getName());
        armorPatternValidator.validate(armorDto, bindingResult);
        if (mbProduct.isEmpty()) {
            bindingResult.rejectValue("", "", "Использован несуществующий товар");
            log.error("Сервис шаблонов предметов сообщает, что для создания шаблона использован несуществующий товар: {}", armorDto.getProduct());
        }
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
