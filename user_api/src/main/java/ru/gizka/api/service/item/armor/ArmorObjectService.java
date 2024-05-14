package ru.gizka.api.service.item.armor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.armor.ArmorObject;
import ru.gizka.api.model.item.armor.ArmorPattern;
import ru.gizka.api.repo.ArmorObjectRepo;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ArmorObjectService {
    private final ArmorObjectRepo armorObjectRepo;

    @Autowired
    public ArmorObjectService(ArmorObjectRepo armorObjectRepo){
        this.armorObjectRepo = armorObjectRepo;
    }

    @Transactional
    public ArmorObject save(ArmorPattern armorPattern, Hero hero){
        log.info("Сервис доспехов сохраняет предмет в инвентарь героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        ArmorObject armorObject = new ArmorObject(armorPattern.getName(), armorPattern.getWeight(), armorPattern.getValue(),
                armorPattern.getArmor(), armorPattern.getDexPenalty(), armorPattern.getArmorType());
        armorObject.setHero(hero);
        return armorObjectRepo.save(armorObject);
    }
}
