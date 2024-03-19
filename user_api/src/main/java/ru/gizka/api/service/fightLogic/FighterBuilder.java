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
        Fighter fighter = modelMapper.map(hero, Fighter.class);
        fighter.setUserLogin(hero.getAppUser().getLogin());
        attributeCalculator.calculate(fighter);
        return fighter;
    }
}
