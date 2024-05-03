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
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.facade.item.ProductFacade;

@RestController
@RequestMapping("/api/admin/product")
@Slf4j
public class AdminProductController {
    private final ProductFacade productFacade;

    @Autowired
    public AdminProductController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @PostMapping()
    public ResponseEntity<ResponseProductDto> create(@Valid @RequestBody RequestProductDto productDto,
                                                     BindingResult bindingResult) {
        log.info("Контроллер товаров администратора принял запрос POST /product: {}", productDto);
        ResponseProductDto responseProduct = productFacade.create(productDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseProduct);
    }
}
