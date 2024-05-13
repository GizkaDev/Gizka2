package ru.gizka.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.creature.ResponseCreatureDto;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.item.armor.ResponseArmorPatternDto;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.dto.user.ResponseAppUserDto;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.model.item.armor.ArmorPattern;
import ru.gizka.api.model.item.armor.ArmorType;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class DtoConverter {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public DtoConverter(ModelMapper modelMapper,
                        ObjectMapper objectMapper) {
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    public ResponseArmorPatternDto getResponseDto(ArmorPattern armorPattern) {
        log.info("Конвертер переводит {} в {}", ArmorPattern.class, ResponseArmorPatternDto.class);
        return new ResponseArmorPatternDto(armorPattern.getName(), armorPattern.getArmor(), armorPattern.getArmorType());
    }

    public ArmorPattern getModel(RequestArmorPatternDto armorDto) {
        log.info("Конвертер переводит {} в {}", RequestArmorPatternDto.class, ArmorPattern.class);
        ArmorType armorType = ArmorType.valueOf(ArmorType.class, armorDto.getArmorType());
        return new ArmorPattern(armorDto.getName(), armorDto.getArmor(), armorType);
    }

    public ResponseItemDto getResponseDto(ItemObject itemObject) {
        log.info("Конвертер переводит {} в {}", ItemPattern.class, ResponseItemDto.class);
        return ResponseItemDto.builder()
                .id(itemObject.getId())
                .name(itemObject.getName())
                .weight(itemObject.getWeight())
                .value(itemObject.getValue())
                .productDto(getResponseDto(itemObject.getProduct()))
                .build();
    }

    public ResponseItemDto getResponseDto(ItemPattern itemPattern) {
        log.info("Конвертер переводит {} в {}", ItemPattern.class, ResponseItemDto.class);
        return ResponseItemDto.builder()
                .id(itemPattern.getId())
                .name(itemPattern.getName())
                .weight(itemPattern.getWeight())
                .value(itemPattern.getValue())
                .productDto(getResponseDto(itemPattern.getProduct()))
                .build();
    }

    public ItemPattern getModel(RequestItemPatternDto itemDto) {
        log.info("Конвертер переводит {} в {}", RequestItemPatternDto.class, ItemPattern.class);
        return ItemPattern.builder()
                .name(itemDto.getName())
                .weight(itemDto.getWeight())
                .value(itemDto.getValue())
                .build();
    }

    public ResponseProductDto getResponseDto(Product product) {
        log.info("Конвертер переводит {} в {}", Product.class, ResponseProductDto.class);
        return ResponseProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    public Product getModel(RequestProductDto productDto) {
        log.info("Конвертер переводит {} в {}", RequestProductDto.class, Product.class);
        return Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();
    }

    public ResponseCreatureDto getResponseDto(Creature creature) {
        log.info("Конвертер переводит {} в {}", Creature.class, ResponseCreatureDto.class);
        return ResponseCreatureDto.builder()
                .id(creature.getId())
                .name(creature.getName())
                .str(creature.getStr())
                .dex(creature.getDex())
                .con(creature.getCon())
                .createdAt(creature.getCreatedAt())
                .race(creature.getRace().getName())
                .minInit(creature.getMinInit())
                .maxInit(creature.getMaxInit())
                .minAttack(creature.getMinAttack())
                .maxAttack(creature.getMaxAttack())
                .minEvasion(creature.getMinEvasion())
                .maxEvasion(creature.getMaxEvasion())
                .minPhysDamage(creature.getMinPhysDamage())
                .maxPhysDamage(creature.getMaxPhysDamage())
                .maxHp(creature.getMaxHp())
                .currentHp(creature.getCurrentHp())
                .endurance(creature.getEndurance())
                .build();
    }

    public Creature getModel(RequestCreatureDto creatureDto) {
        log.info("Конвертер переводит {} в {}", RequestCreatureDto.class, Creature.class);
        return Creature.builder()
                .name(creatureDto.getName())
                .str(creatureDto.getStr())
                .dex(creatureDto.getDex())
                .con(creatureDto.getCon())
                .build();
    }

    public ResponseRaceDto getResponseDto(Race race) {
        log.info("Конвертер переводит {} в {}", Race.class, ResponseRaceDto.class);
        return ResponseRaceDto.builder()
                .id(race.getId())
                .name(race.getName())
                .createdAt(race.getCreatedAt())
                .isPlayable(race.getIsPlayable())
                .strBonus(race.getStrBonus())
                .dexBonus(race.getDexBonus())
                .conBonus(race.getConBonus())
                .wisBonus(race.getWisBonus())
                .build();
    }

    public Race getModel(RequestRaceDto raceDto) {
        log.info("Конвертер переводит {} в {}", RequestRaceDto.class, Race.class);
        return Race.builder()
                .name(raceDto.getName())
                .isPlayable(raceDto.getIsPlayable())
                .strBonus(raceDto.getStrBonus())
                .dexBonus(raceDto.getDexBonus())
                .conBonus(raceDto.getConBonus())
                .wisBonus(raceDto.getWisBonus())
                .build();
    }

    public AppUser getModel(RequestAppUserDto userDto) {
        log.info("Конвертер переводит {} в {} для пользователя: {}", RequestAppUserDto.class, AppUser.class, userDto.getLogin());
        return AppUser.builder()
                .login(userDto.getLogin())
                .password(userDto.getPassword())
                .build();
    }

    public ResponseAppUserDto getResponseDto(AppUser appUser) {
        log.info("Конвертер переводит {} в {} для пользователя: {}", AppUser.class, ResponseAppUserDto.class, appUser.getLogin());
        return ResponseAppUserDto.builder()
                .id(appUser.getId())
                .login(appUser.getLogin())
                .registeredAt(appUser.getRegisteredAt())
                .roles(appUser.getRoles())
                .build();
    }

    public Hero getModel(RequestHeroDto heroDto) {
        log.info("Конвертер переводит {} в {}", RequestHeroDto.class, Hero.class);
        return Hero.builder()
                .name(heroDto.getName())
                .lastname(heroDto.getLastName())
                .str(heroDto.getStr())
                .dex(heroDto.getDex())
                .con(heroDto.getCon())
                .wis(heroDto.getWis())
                .build();
    }

    public ResponseHeroDto getResponseDto(Hero hero) {
        log.info("Конвертер переводит {} в {}", Hero.class, ResponseHeroDto.class);
        return ResponseHeroDto.builder()
                .id(hero.getId())
                .name(hero.getName())
                .lastname(hero.getLastname())

                .str(hero.getStr())
                .dex(hero.getDex())
                .con(hero.getCon())
                .wis(hero.getWis())

                .minInit(hero.getMinInit())
                .maxInit(hero.getMaxInit())
                .minAttack(hero.getMinAttack())
                .maxAttack(hero.getMaxAttack())
                .minEvasion(hero.getMinEvasion())
                .maxEvasion(hero.getMaxEvasion())
                .minPhysDamage(hero.getMinPhysDamage())
                .maxPhysDamage(hero.getMaxPhysDamage())
                .maxHp(hero.getMaxHp())
                .endurance(hero.getEndurance())
                .maxWeight(hero.getMaxWeight())
                .search(hero.getSearch())
                .treat(hero.getTreat())

                .currentHp(hero.getCurrentHp())
                .currentWeight(hero.getCurrentWeight())

                .createdAt(hero.getCreatedAt())
                .userLogin(hero.getAppUser().getLogin())
                .status(hero.getStatus().name())
                .race(hero.getRace().getName())
                .treatAt(hero.getTreatAt())
                .build();
    }

    public DuelDto getResponseDto(Duel duel) {
        log.info("Конвертер переводит {} в {}", Duel.class, DuelDto.class);
        List<Turn> turns;
        try {
            turns = objectMapper.readValue(duel.getTurns(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<Fighter> heroFighters = duel.getHeroes().stream()
                .map(Fighter::new)
                .toList();
        return DuelDto.builder()
                .id(duel.getId())
                .heroFighters(heroFighters)
                .turns(turns)
                .result(duel.getResult().name())
                .createdAt(duel.getCreatedAt())
                .build();
    }

    public FightDto getResponseDto(Fight fight) {
        log.info("Конвертер переводит {} в {}", Fight.class, FightDto.class);
        List<Turn> turns;
        try {
            turns = objectMapper.readValue(fight.getTurns(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ResponseHeroDto responseHeroDto = getResponseDto(fight.getHero());
        ResponseCreatureDto responseCreatureDto = getResponseDto(fight.getCreature());
        FightDto fightDto = FightDto.builder()
                .id(fight.getId())
                .heroFighter(responseHeroDto)
                .creatureFighter(responseCreatureDto)
                .turns(turns)
                .result(fight.getResult().name())
                .createdAt(fight.getCreatedAt())
                .build();
        if (fight.getLoot() != null) {
            List<ResponseItemDto> loot = fight.getLoot().isEmpty() ? Collections.emptyList() :
                    fight.getLoot().stream()
                            .map(this::getResponseDto)
                            .toList();
            fightDto.setLoot(loot);
        }
        return fightDto;
    }

    public NotificationDto getResponseDto(Notification notification) {
        log.info("Конвертер переводит {} в {}", Notification.class, NotificationDto.class);
        return modelMapper.map(notification, NotificationDto.class);
    }
}
