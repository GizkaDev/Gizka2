package ru.gizka.api.facade.item;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.item.RequestItemDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.model.item.Item;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.service.item.ItemService;
import ru.gizka.api.service.item.ProductService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.ItemValidator;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ItemFacade {
    private final ItemService itemService;
    private final ItemValidator itemValidator;
    private final DtoConverter dtoConverter;
    private final ProductService productService;

    @Autowired
    public ItemFacade(ItemService itemService,
                      ItemValidator itemValidator,
                      DtoConverter dtoConverter,
                      ProductService productService) {
        this.itemService = itemService;
        this.itemValidator = itemValidator;
        this.dtoConverter = dtoConverter;
        this.productService = productService;
    }

    public ResponseItemDto create(RequestItemDto itemDto, BindingResult bindingResult) {
        log.info("Сервис предметов начинает создание нового предмета: {}", itemDto.getName());
        Optional<Product> mbProduct = productService.getByName(itemDto.getProduct());
        checkValues(itemDto, bindingResult, mbProduct);
        Item item = itemService.create(dtoConverter.getModel(itemDto), mbProduct.get());
        return dtoConverter.getResponseDto(item);
    }

    public ResponseItemDto getByName(String name) {
        log.info("Сервис предметов начинает поиск предмета: {}", name);
        Item item = itemService.getByName(name)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Предмет не найден: %s", name)));
        return dtoConverter.getResponseDto(item);
    }

    private void checkValues(RequestItemDto itemDto,
                             BindingResult bindingResult,
                             Optional<Product> mbProduct) {
        log.info("Сервис предметов начинает проверку валидности нового предмета: {}", itemDto.getName());
        itemValidator.validate(itemDto, bindingResult);
        if (mbProduct.isEmpty()) {
            bindingResult.rejectValue("", "", "Использован несуществующий товар");
            log.error("Сервис предметов сообщает, что для создания предмета использован несуществующий товар: {}", itemDto.getProduct());
        }
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
