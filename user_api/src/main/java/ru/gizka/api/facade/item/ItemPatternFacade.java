package ru.gizka.api.facade.item;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.weapon.RequestWeaponPatternDto;
import ru.gizka.api.dto.item.weapon.ResponseWeaponDto;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.model.item.weapon.WeaponPattern;
import ru.gizka.api.service.item.ItemPatternService;
import ru.gizka.api.service.item.ProductService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.ItemPatternValidator;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ItemPatternFacade {
    private final ItemPatternService itemPatternService;
    private final ItemPatternValidator itemPatternValidator;
    private final DtoConverter dtoConverter;
    private final ProductService productService;

    @Autowired
    public ItemPatternFacade(ItemPatternService itemPatternService,
                             ItemPatternValidator itemPatternValidator,
                             DtoConverter dtoConverter,
                             ProductService productService) {
        this.itemPatternService = itemPatternService;
        this.itemPatternValidator = itemPatternValidator;
        this.dtoConverter = dtoConverter;
        this.productService = productService;
    }

    public ResponseItemDto create(RequestItemPatternDto itemDto, BindingResult bindingResult) {
        log.info("Сервис шаблонов предметов начинает создание нового шаблона: {}", itemDto.getName());
        Optional<Product> mbProduct = productService.getByName(itemDto.getProduct());
        checkValues(itemDto, bindingResult, mbProduct);
        ItemPattern itemPattern = itemPatternService.create(dtoConverter.getModel(itemDto), mbProduct.get());
        return dtoConverter.getResponseDto(itemPattern);
    }

    public ResponseWeaponDto create(RequestWeaponPatternDto weaponDto, BindingResult bindingResult) {
        Optional<Product> mbProduct = productService.getByName("Оружие");
        checkValues(weaponDto, bindingResult, mbProduct);
        WeaponPattern weaponPattern = itemPatternService.create(dtoConverter.getModel(weaponDto, mbProduct.get()));
        return dtoConverter.getResponseDto(weaponPattern);
    }

    public ResponseItemDto getByName(String name) {
        log.info("Сервис шаблонов предметов начинает поиск шаблона: {}", name);
        ItemPattern itemPattern = itemPatternService.getByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Шаблон не найден: %s", name)));
        return dtoConverter.getResponseDto(itemPattern);
    }

    private void checkValues(RequestItemPatternDto itemDto,
                             BindingResult bindingResult,
                             Optional<Product> mbProduct) {
        log.info("Сервис шаблонов предметов начинает проверку валидности нового шаблона: {}", itemDto.getName());
        itemPatternValidator.validate(itemDto, bindingResult);
        if (mbProduct.isEmpty()) {
            bindingResult.rejectValue("", "", "Использован несуществующий товар");
            log.error("Сервис шаблонов предметов сообщает, что для создания шаблона использован несуществующий товар: {}", itemDto.getProduct());
        }
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
