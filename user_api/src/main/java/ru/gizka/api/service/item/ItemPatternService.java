package ru.gizka.api.service.item;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.repo.ItemPatternRepo;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemPatternService {
    private final ItemPatternRepo itemPatternRepo;
    private final Random random;

    @Autowired
    public ItemPatternService(ItemPatternRepo itemPatternRepo,
                              Random random) {
        this.itemPatternRepo = itemPatternRepo;
        this.random = random;
    }

    @Transactional
    public ItemPattern create(ItemPattern itemPattern, Product product) {
        log.info("Сервис шаблонов предметов сохраняет новый предмет: {}", itemPattern.getName());
        itemPattern.setProduct(product);
        itemPattern.setFights(new ArrayList<>());
        return itemPatternRepo.save(itemPattern);
    }

    public Optional<ItemPattern> getByName(String name) {
        log.info("Сервис шаблонов предметов ищет предмет: {}", name);
        return itemPatternRepo.findByName(name);
    }

    public Optional<ItemPattern> getRandom() {
        log.info("Сервис шаблонов предметов ищет случайный предмет");
        return itemPatternRepo.findRandom();
    }

    public List<ItemPattern> getRandomLootPattern(Fight fight) {
        if (Objects.requireNonNull(fight.getResult()) == Result.ATTACKER) {
            List<ItemPattern> loot = new ArrayList<>();
            int num = random.nextInt(1, 10); // можно сделать от характеристики или навыка
            for (int i = 0; i < num; i++) {
                getRandom().ifPresent(loot::add);
            }
            return loot;
        }
        return null;
    }

    @Transactional
    public ItemPattern save(ItemPattern itemPattern) {
        log.info("Сервис шаблонов предметов сохраняет предмет: {}", itemPattern.getName());
        return itemPatternRepo.save(itemPattern);
    }

    public ItemPattern getByNameWithFights(String name) {
        log.info("Сервис шаблонов предметов ищет шаблон: {}", name);
        return itemPatternRepo.findByNameWithFights(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Шаблон %s не найден", name)));
    }
}
