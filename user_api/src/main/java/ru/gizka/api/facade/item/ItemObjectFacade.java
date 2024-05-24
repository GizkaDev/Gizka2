package ru.gizka.api.facade.item;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.actionLogic.HeroActionLogic;
import ru.gizka.api.service.item.ItemObjectService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class ItemObjectFacade {
    private final HeroService heroService;
    private final ItemObjectService itemObjectService;
    private final DtoConverter dtoConverter;
    private final HeroActionLogic heroActionLogic;

    @Autowired
    public ItemObjectFacade(HeroService heroService,
                            ItemObjectService itemObjectService,
                            DtoConverter dtoConverter,
                            HeroActionLogic heroActionLogic) {
        this.heroService = heroService;
        this.itemObjectService = itemObjectService;
        this.dtoConverter = dtoConverter;
        this.heroActionLogic = heroActionLogic;
    }

    public List<ResponseItemDto> getInventory(AppUser appUser) {
        log.info("Сервис предметов начинает поиск инвентаря текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        List<ItemObject> inventory = itemObjectService.getByHero(heroes.get(0));
        return inventory.stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }

    public void deleteFromInventory(AppUser appUser, Long id) {
        log.info("Сервис предметов начинает удаление предмета: {} из  инвентаря текущего героя для пользователя: {}", id, appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        ItemObject itemToDrop = itemObjectService.getById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Предмет не найден"));
        if (itemToDrop.getHero() == null) {
            throw new EntityNotFoundException("Предмет не найден в инвентаре");
        }
        if (itemToDrop.getHero().getId().equals(heroes.get(0).getId())) {
            heroActionLogic.dropItem(heroes.get(0), itemToDrop);
        } else {
            throw new EntityNotFoundException("Предмет не найден в инвентаре");
        }
    }
}
