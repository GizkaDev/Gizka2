package ru.gizka.api.facade.old.item.armor;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.old.hero.ResponseHeroDto;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.old.item.armor.ArmorObject;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.old.HeroService;
import ru.gizka.api.service.old.actionLogic.HeroActionLogic;
import ru.gizka.api.service.old.item.ItemObjectService;
import ru.gizka.api.service.old.item.armor.ArmorObjectService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class ArmorObjectFacade {
    private final HeroService heroService;
    private final ArmorObjectService armorObjectService;
    private final HeroActionLogic heroActionLogic;
    private final DtoConverter dtoConverter;
    private final ItemObjectService itemObjectService;

    @Autowired
    public ArmorObjectFacade(HeroService heroService,
                             ArmorObjectService armorObjectService,
                             DtoConverter dtoConverter,
                             HeroActionLogic heroActionLogic,
                             ItemObjectService itemObjectService) {
        this.heroService = heroService;
        this.armorObjectService = armorObjectService;
        this.dtoConverter = dtoConverter;
        this.heroActionLogic = heroActionLogic;
        this.itemObjectService = itemObjectService;
    }

    public ResponseHeroDto equipArmor(AppUser appUser, Long id) {
        log.info("Сервис доспехов начинает поиск текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        ArmorObject armorToEquip = armorObjectService.getById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Предмет не найден"));
        if (armorToEquip.getHero().getId().equals(heroes.get(0).getId())) {
            Hero heroToSave = heroActionLogic.equipArmor(heroes.get(0), armorToEquip);
            Hero hero = heroService.save(heroToSave);
            return dtoConverter.getResponseDto(hero);
        }
        throw new IllegalArgumentException("Такого предмета нет в инвентаре");
    }

    public ResponseHeroDto takeOffArmor(AppUser appUser) {
        log.info("Сервис доспехов начинает поиск текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        if (heroes.get(0).getEquippedArmor() == null) {
            throw new EntityNotFoundException("У героя не экипирован доспех");
        }
        return dtoConverter.getResponseDto(heroService.save(heroActionLogic.takeOffArmor(heroes.get(0))));
    }
}
