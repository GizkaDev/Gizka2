package ru.gizka.api.service.old.item.armor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.model.old.item.armor.ArmorPattern;
import ru.gizka.api.repo.old.ArmorPatternRepo;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ArmorPatternService {
    private final ArmorPatternRepo armorPatternRepo;

    @Autowired
    public ArmorPatternService(ArmorPatternRepo armorPatternRepo){
        this.armorPatternRepo = armorPatternRepo;
    }

    @Transactional
    public ArmorPattern create(ArmorPattern armorPattern){
        log.info("Сервис шаблонов доспехов сохраняет новый доспех: {}", armorPattern.getName());
        armorPattern.setFights(new ArrayList<>());
        return armorPatternRepo.save(armorPattern);
    }

    public Optional<ArmorPattern> getByName(String name){
        log.info("Сервис шаблонов доспехов ищет доспех: {}", name);
        return armorPatternRepo.findByName(name);
    }
}
