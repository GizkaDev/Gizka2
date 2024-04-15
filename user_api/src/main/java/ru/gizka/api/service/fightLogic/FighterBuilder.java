package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.hero.Hero;

@Service
@Slf4j
public class FighterBuilder {
    private final ModelMapper modelMapper;
    private final AttributeCalculator attributeCalculator;

    @Autowired
    public FighterBuilder(ModelMapper modelMapper,
                          AttributeCalculator attributeCalculator){
        this.modelMapper = modelMapper;
        this.attributeCalculator = attributeCalculator;
    }

    public Fighter build(Hero hero){
        log.info("Сервис сборки бойцов готовит бойца: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        Fighter fighter = Fighter.builder()
                .name(hero.getName())
                .lastname(hero.getLastname())
                .createdAt(hero.getCreatedAt())
                .userLogin(hero.getAppUser().getLogin())
                .status(hero.getStatus().name())
                .str(hero.getStr())
                .dex(hero.getDex())
                .con(hero.getCon())
                .build();
        attributeCalculator.calculate(fighter);
        return fighter;
    }
}
