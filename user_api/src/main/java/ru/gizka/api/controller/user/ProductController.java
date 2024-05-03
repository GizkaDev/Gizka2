package ru.gizka.api.controller.user;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.facade.item.ProductFacade;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Slf4j
public class ProductController {
    private final ProductFacade productFacade;

    @Autowired
    public ProductController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseProductDto> getByName(@PathVariable @Size(min = 1, max = 100) String name) {
        log.info("Контроллер товаров принял запрос GET /{name}");
        return ResponseEntity.ok(productFacade.getByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseProductDto>> getAll() {
        log.info("Контроллер товаров принял запрос GET /all");
        return ResponseEntity.ok(productFacade.getAll());
    }
}
