package ru.gizka.api.facade.item;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.service.item.ProductService;
import ru.gizka.api.util.DtoConverter;
import ru.gizka.api.util.validator.ProductValidator;

import java.util.List;

@Service
@Slf4j
public class ProductFacade {
    private final ProductService productService;
    private final ProductValidator productValidator;
    private final DtoConverter dtoConverter;


    @Autowired
    public ProductFacade(ProductService productService,
                         ProductValidator productValidator,
                         DtoConverter dtoConverter){
        this.productService = productService;
        this.productValidator = productValidator;
        this.dtoConverter = dtoConverter;
    }

   public ResponseProductDto create(RequestProductDto productDto, BindingResult bindingResult){
        log.info("Сервис товаров начинает создание нового товара: {}", productDto);
        checkValues(productDto, bindingResult);
        Product product = productService.create(dtoConverter.getModel(productDto));
        return dtoConverter.getResponseDto(product);
   }

    private void checkValues(RequestProductDto productDto,
                             BindingResult bindingResult) {
        log.info("Сервис товаров начинает проверку валидности нового товара: {}", productDto);
        productValidator.validate(productDto, bindingResult);
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            throw new ValidationException(errors.toString());
        }
    }
}
