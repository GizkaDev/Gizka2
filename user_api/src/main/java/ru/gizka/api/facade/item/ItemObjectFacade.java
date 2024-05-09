package ru.gizka.api.facade.item;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.HeroService;
import ru.gizka.api.service.item.ItemObjectService;
import ru.gizka.api.util.DtoConverter;

import java.util.List;

@Service
@Slf4j
public class ItemObjectFacade {
    private final HeroService heroService;
    private final ItemObjectService itemObjectService;
    private final DtoConverter dtoConverter;

    @Autowired
    public ItemObjectFacade(HeroService heroService,
                            ItemObjectService itemObjectService,
                            DtoConverter dtoConverter){
        this.heroService = heroService;
        this.itemObjectService = itemObjectService;
        this.dtoConverter = dtoConverter;
    }

    public List<ResponseItemDto> getInventory(AppUser appUser) {
        log.info("Сервис героев начинает поиск текущего героя для пользователя: {}", appUser.getLogin());
        List<Hero> heroes = heroService.getAliveByUser(appUser);
        if (heroes.isEmpty()) {
            throw new EntityNotFoundException("У пользователя нет героя со статусом ALIVE.");
        }
        List<ItemObject> inventory = itemObjectService.getByHero(heroes.get(0));
        return inventory.stream()
                .map(dtoConverter::getResponseDto)
                .toList();
    }
}
