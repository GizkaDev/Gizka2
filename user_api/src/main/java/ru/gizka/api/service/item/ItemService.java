package ru.gizka.api.service.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.item.Item;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.repo.ItemRepo;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemService {
    private final ItemRepo itemRepo;

    @Autowired
    public ItemService(ItemRepo itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Transactional
    public Item create(Item item, Product product) {
        log.info("Сервис предметов сохраняет новый предмет: {}", item.getName());
        item.setProduct(product);
        return itemRepo.save(item);
    }

    public Optional<Item> getByName(String name) {
        log.info("Сервис предметов ищет предмет: {}", name);
        return itemRepo.findByName(name);
    }
}
