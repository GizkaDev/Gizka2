package ru.gizka.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.dto.appUser.ResponseAppUserDto;
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
import ru.gizka.api.dto.item.armor.ResponseArmorDto;
import ru.gizka.api.dto.item.weapon.RequestWeaponPatternDto;
import ru.gizka.api.dto.item.weapon.ResponseWeaponDto;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;
import ru.gizka.api.model.item.armor.ArmorObject;
import ru.gizka.api.model.item.armor.ArmorPattern;
import ru.gizka.api.model.item.armor.ArmorType;
import ru.gizka.api.model.item.weapon.WeaponPattern;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.race.RaceSize;

import java.util.ArrayList;
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

    public ResponseWeaponDto getResponseDto(WeaponPattern weaponPattern) {
        logConverting(WeaponPattern.class, ResponseWeaponDto.class);
        return new ResponseWeaponDto(
                weaponPattern.getId(),
                weaponPattern.getName(),
                weaponPattern.getWeight(),
                weaponPattern.getValue(),
                getResponseDto(weaponPattern.getProduct()),
                weaponPattern.getDamageMult(),
                weaponPattern.getIsTwoHanded(),
                weaponPattern.getDamageTypes());
    }

    public WeaponPattern getModel(RequestWeaponPatternDto weaponDto, Product product) {
        logConverting(RequestWeaponPatternDto.class, WeaponPattern.class);
        return new WeaponPattern(
                weaponDto.getName(),
                weaponDto.getWeight(),
                weaponDto.getValue(),
                product,
                new ArrayList<Fight>(),
                weaponDto.getDamageMult(),
                weaponDto.getIsTwoHanded(),
                weaponDto.getDamageTypes());
    }

    public ResponseArmorDto getResponseDto(ArmorObject armorObject) {
        logConverting(ArmorObject.class, ResponseArmorDto.class);
        return new ResponseArmorDto(
                armorObject.getId(),
                armorObject.getName(),
                armorObject.getWeight(),
                armorObject.getValue(),
                getResponseDto(armorObject.getProduct() == null ? new Product(0L, "Тряпье", 0L, null, null) : armorObject.getProduct()),
                armorObject.getArmor(),
                armorObject.getDexPenalty(),
                armorObject.getArmorType());
    }

    public ResponseArmorDto getResponseDto(ArmorPattern armorPattern) {
        logConverting(ArmorPattern.class, ResponseArmorDto.class);
        return new ResponseArmorDto(armorPattern.getId(), armorPattern.getName(), armorPattern.getWeight(), armorPattern.getValue(), getResponseDto(armorPattern.getProduct()),
                armorPattern.getArmor(), armorPattern.getDexPenalty(), armorPattern.getArmorType());
    }

    public ArmorPattern getModel(RequestArmorPatternDto armorDto) {
        logConverting(RequestArmorPatternDto.class, ArmorPattern.class);
        ArmorType armorType = ArmorType.valueOf(ArmorType.class, armorDto.getArmorType());
        return new ArmorPattern(armorDto.getName(), armorDto.getWeight(), armorDto.getValue(),
                armorDto.getArmor(), armorDto.getDexPenalty(), armorType);
    }

    public ResponseItemDto getResponseDto(ItemObject itemObject) {
        logConverting(ItemObject.class, ResponseItemDto.class);
        return new ResponseItemDto(
                itemObject.getId(),
                itemObject.getName(),
                itemObject.getWeight(),
                itemObject.getValue(),
                getResponseDto(itemObject.getProduct()));
    }

    public ResponseItemDto getResponseDto(ItemPattern itemPattern) {
        logConverting(ItemPattern.class, ResponseItemDto.class);
        return new ResponseItemDto(
                itemPattern.getId(),
                itemPattern.getName(),
                itemPattern.getWeight(),
                itemPattern.getValue(),
                getResponseDto(itemPattern.getProduct()));
    }

    public ItemPattern getModel(RequestItemPatternDto itemDto) {
        logConverting(RequestItemPatternDto.class, ItemPattern.class);
        return new ItemPattern(
                itemDto.getName(),
                itemDto.getWeight(),
                itemDto.getValue());
    }

    public ResponseProductDto getResponseDto(Product product) {
        logConverting(Product.class, ResponseProductDto.class);
        return new ResponseProductDto(
                product.getId(),
                product.getName(),
                product.getPrice());
    }

    public Product getModel(RequestProductDto productDto) {
        logConverting(RequestProductDto.class, Product.class);
        return new Product(
                productDto.getName(),
                productDto.getPrice());
    }

    public ResponseCreatureDto getResponseDto(Creature creature) {
        logConverting(Creature.class, ResponseCreatureDto.class);
        return new ResponseCreatureDto(
                creature.getId(),
                creature.getName(),
                creature.getCreatedAt(),
                creature.getRace().getName(),

                creature.getStr(),
                creature.getDex(),
                creature.getCon(),

                creature.getMinInit(),
                creature.getMaxInit(),
                creature.getMinAttack(),
                creature.getMaxAttack(),
                creature.getMinEvasion(),
                creature.getMaxEvasion(),
                creature.getMinPhysDamage(),
                creature.getMaxPhysDamage(),
                creature.getMaxHp(),
                creature.getEndurance(),

                creature.getCurrentHp(),
                creature.getDef());
    }

    public Creature getModel(RequestCreatureDto creatureDto) {
        logConverting(RequestCreatureDto.class, Creature.class);
        return new Creature(
                creatureDto.getName(),
                creatureDto.getStr(),
                creatureDto.getDex(),
                creatureDto.getCon(),
                creatureDto.getDef());
    }

    public ResponseRaceDto getResponseDto(Race race) {
        logConverting(Race.class, ResponseRaceDto.class);
        return new ResponseRaceDto(
                race.getId(),
                race.getName(),
                race.getCreatedAt(),
                race.getIsPlayable(),
                race.getStrBonus(),
                race.getDexBonus(),
                race.getConBonus(),
                race.getWisBonus(),
                race.getDefBonus(),
                race.getRaceSize());
    }

    public Race getModel(RequestRaceDto raceDto) {
        logConverting(RequestRaceDto.class, Race.class);
        return new Race(
                raceDto.getName(),
                raceDto.getIsPlayable(),
                raceDto.getStrBonus(),
                raceDto.getDexBonus(),
                raceDto.getConBonus(),
                raceDto.getWisBonus(),
                raceDto.getDefBonus(),
                RaceSize.valueOf(RaceSize.class, raceDto.getRaceSize()));
    }

    public AppUser getModel(RequestAppUserDto userDto) {
        logConverting(RequestAppUserDto.class, AppUser.class);
        return new AppUser(
                userDto.getLogin(),
                userDto.getPassword());
    }

    public ResponseAppUserDto getResponseDto(AppUser appUser) {
        logConverting(AppUser.class, ResponseAppUserDto.class);
        return new ResponseAppUserDto(
                appUser.getId(),
                appUser.getLogin(),
                appUser.getRegisteredAt(),
                appUser.getRoles());
    }

    public Hero getModel(RequestHeroDto heroDto) {
        logConverting(RequestHeroDto.class, Hero.class);
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
        logConverting(Hero.class, ResponseHeroDto.class);
        return new ResponseHeroDto(
                hero.getId(),
                hero.getName(),
                hero.getLastname(),

                hero.getStr(),
                hero.getDex(),
                hero.getCon(),
                hero.getWis(),

                hero.getMinInit(),
                hero.getMaxInit(),
                hero.getMinAttack(),
                hero.getMaxAttack(),
                hero.getMinEvasion(),
                hero.getMaxEvasion(),
                hero.getMinPhysDamage(),
                hero.getMaxPhysDamage(),
                hero.getMaxHp(),
                hero.getEndurance(),
                hero.getMaxWeight(),
                hero.getSearch(),
                hero.getTreat(),

                hero.getCurrentHp(),
                hero.getCurrentWeight(),
                hero.getDef(),

                getResponseDto(hero.getEquippedArmor() == null ? new ArmorObject(
                        "Без доспеха",
                        0L,
                        0,
                        0,
                        0,
                        ArmorType.CLOTHES) : hero.getEquippedArmor()),

                hero.getCreatedAt(),
                hero.getAppUser().getLogin(),
                hero.getStatus().name(),
                hero.getRace().getName(),
                hero.getTreatAt());
    }

    public DuelDto getResponseDto(Duel duel) {
        logConverting(Duel.class, DuelDto.class);
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
        return new DuelDto(
                duel.getId(),
                heroFighters,
                turns,
                duel.getResult().name(),
                duel.getCreatedAt());
    }

    public FightDto getResponseDto(Fight fight) {
        logConverting(Fight.class, FightDto.class);
        List<Turn> turns;
        try {
            turns = objectMapper.readValue(fight.getTurns(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ResponseHeroDto responseHeroDto = getResponseDto(fight.getHero());
        ResponseCreatureDto responseCreatureDto = getResponseDto(fight.getCreature());
        return new FightDto(
                fight.getId(),
                responseHeroDto,
                responseCreatureDto,
                turns,
                fight.getResult().name(),
                fight.getCreatedAt(),
                fight.getLoot().isEmpty() ?
                        Collections.emptyList() :
                        fight.getLoot().stream()
                                .map(this::getResponseDto)
                                .toList());
    }

    public NotificationDto getResponseDto(Notification notification) {
        logConverting(Notification.class, NotificationDto.class);
        return modelMapper.map(notification, NotificationDto.class);
    }

    private <T, R> void logConverting(Class<T> source, Class<R> destClass) {
        log.info("Переводим {} в {}", source.getSimpleName(), destClass.getSimpleName());
    }
}
