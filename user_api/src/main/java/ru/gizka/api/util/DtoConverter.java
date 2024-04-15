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
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.dto.user.ResponseAppUserDto;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.fightLogic.FighterBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DtoConverter {
    private final ModelMapper modelMapper;
    private final FighterBuilder fighterBuilder;
    private final ObjectMapper objectMapper;

    @Autowired
    public DtoConverter(ModelMapper modelMapper,
                        FighterBuilder fighterBuilder,
                        ObjectMapper objectMapper) {
        this.modelMapper = modelMapper;
        this.fighterBuilder = fighterBuilder;
        this.objectMapper = objectMapper;
    }

    public ResponseCreatureDto getResponseDto(Creature creature) {
        log.info("Конвертер переводит {} в {}", Creature.class, RequestCreatureDto.class);
        return ResponseCreatureDto.builder()
                .id(creature.getId())
                .name(creature.getName())
                .str(creature.getStr())
                .dex(creature.getDex())
                .con(creature.getCon())
                .createdAt(creature.getCreatedAt())
                .race(creature.getRace().getName())
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
                .build();
    }

    public Race getModel(RequestRaceDto raceDto) {
        log.info("Конвертер переводит {} в {}", RequestRaceDto.class, Race.class);
        return Race.builder()
                .name(raceDto.getName())
                .isPlayable(raceDto.getIsPlayable())
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
                .createdAt(hero.getCreatedAt())
                .userLogin(hero.getAppUser().getLogin())
                .status(hero.getStatus().name())
                .race(hero.getRace().getName())
                .minInit(hero.getMinInit())
                .maxInit(hero.getMaxInit())
                .minAttack(hero.getMinAttack())
                .maxAttack(hero.getMaxAttack())
                .minEvasion(hero.getMinEvasion())
                .maxEvasion(hero.getMaxEvasion())
                .minPhysDamage(hero.getMinPhysDamage())
                .maxPhysDamage(hero.getMaxPhysDamage())
                .maxHp(hero.getMaxHp())
                .currentHp(hero.getCurrentHp())
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
        List<Fighter> fighters = duel.getHeroes().stream()
                .map(fighterBuilder::build)
                .toList();
        return DuelDto.builder()
                .id(duel.getId())
                .fighters(fighters)
                .turns(turns)
                .result(duel.getResult().name())
                .createdAt(duel.getCreatedAt())
                .build();
    }

    public NotificationDto getResponseDto(Notification notification) {
        log.info("Конвертер переводит {} в {}", Notification.class, NotificationDto.class);
        return modelMapper.map(notification, NotificationDto.class);
    }
}
