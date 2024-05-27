package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.old.item.RequestProductDto;
import ru.gizka.api.service.old.item.ProductService;

@Component
@Slf4j
public class ProductValidator implements Validator {
    private final ProductService productService;

    @Autowired
    public ProductValidator(ProductService productService) {
        this.productService = productService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return RequestProductDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestProductDto productDto = (RequestProductDto) target;
        log.info("Валидатор товаров проверяет новый товар: {}", productDto);
        validateCreate(productDto, errors);
    }

    private void validateCreate(RequestProductDto productDto, Errors errors) {
        if(productService.getByName(productDto.getName()).isPresent()){
            errors.rejectValue("name", "", "Название товара уже существует");
            log.info("Валидатор товаров сообщает, что название товара уже существует: {}", productDto.getName());
        }

        if(productDto.getName() == null || productDto.getName().equals("null")){
            errors.rejectValue("name", "", "Недопустимое название");
            log.info("Валидатор товаров сообщает, что название недопустимо: {}", productDto.getName());
        }
    }
}
