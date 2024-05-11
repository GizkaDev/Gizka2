package ru.gizka.api.service.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.repo.ItemObjectRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemObjectService {
    private final ItemObjectRepo itemObjectRepo;

    @Autowired
    public ItemObjectService(ItemObjectRepo itemObjectRepo) {
        this.itemObjectRepo = itemObjectRepo;
    }

    @Transactional
    public ItemObject save(ItemPattern itemPattern, Hero hero) {
        log.info("Сервис предметов сохраняет предмет в инвентарь героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        ItemObject itemObject = new ItemObject(itemPattern.getName(),
                itemPattern.getWeight(),
                itemPattern.getValue(),
                itemPattern.getProduct(),
                hero);
        return itemObjectRepo.save(itemObject);
    }

    public List<ItemObject> getAll(){
        log.info("Сервис предметов ищет все предметы");
        return itemObjectRepo.findAll();
    }

    public List<ItemObject> getByHero(Hero hero){
        log.info("Сервис предметов ищет инвентарь героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return itemObjectRepo.findByHero(hero);
    }

    @Transactional
    public void delete(ItemObject itemObject){
        log.info("Сервис предметов удаляет предмет: {} с id: {}", itemObject.getName(), itemObject.getId());
        itemObjectRepo.delete(itemObject);
    }
}
