package ru.gizka.api.service.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.repo.ProductRepo;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Transactional
    public Product create(Product product){
        log.info("Сервис товаров сохраняет новый товар: {}", product);
        return productRepo.save(product);
    }

    public Optional<Product> getByName(String name) {
        log.info("Сервис товаров ищет товар: {}", name);
        return productRepo.findByName(name);
    }
}
