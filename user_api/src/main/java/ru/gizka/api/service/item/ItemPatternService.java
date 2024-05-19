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
import ru.gizka.api.model.item.weapon.WeaponPattern;
import ru.gizka.api.repo.ItemPatternRepo;
import ru.gizka.api.util.RandomRoller;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemPatternService {
    private final ItemPatternRepo itemPatternRepo;
    private final RandomRoller randomRoller;

    @Autowired
    public ItemPatternService(ItemPatternRepo itemPatternRepo,
                              RandomRoller randomRoller) {
        this.itemPatternRepo = itemPatternRepo;
        this.randomRoller = randomRoller;
    }

    @Transactional
    public ItemPattern create(ItemPattern itemPattern, Product product) {
        log.info("Сервис шаблонов предметов сохраняет новый предмет: {}", itemPattern.getName());
        itemPattern.setProduct(product);
        itemPattern.setFights(new ArrayList<>());
        return itemPatternRepo.save(itemPattern);
    }

    @Transactional
    public WeaponPattern create(WeaponPattern weaponPattern) {
        log.info("Сервис шаблонов предметов сохраняет новый предмет: {}", weaponPattern.getName());
        return itemPatternRepo.save(weaponPattern);
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
        int num = randomRoller.rollSearch(0, fight.getHero().getSearch()) / 4;
        if (Objects.requireNonNull(fight.getResult()) == Result.ATTACKER || num == 0) {
            List<ItemPattern> loot = new ArrayList<>();
            for (int i = 1; i <= num; i++) {
                getRandom().ifPresent(loot::add);
            }
            return loot;
        }
        return Collections.emptyList();
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
