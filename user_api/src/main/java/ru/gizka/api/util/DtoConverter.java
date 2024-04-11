package ru.gizka.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.race.ResponseRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.dto.user.ResponseAppUserDto;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.fightLogic.FighterBuilder;

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

    public ResponseRaceDto getResponseDto(Race race) {
        log.info("Конвертер переводит {} в {}", Race.class, ResponseRaceDto.class);
        return modelMapper.map(race, ResponseRaceDto.class);
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
        return modelMapper.map(appUser, ResponseAppUserDto.class);
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
        ResponseHeroDto heroDto = modelMapper.map(hero, ResponseHeroDto.class);
        heroDto.setUserLogin(hero.getAppUser().getLogin());
        heroDto.setRace(hero.getRace().getName());
        return heroDto;
    }

    public DuelDto getResponseDto(Duel duel) {
        log.info("Конвертер переводит {} в {}", Duel.class, DuelDto.class);
        DuelDto duelDto = modelMapper.map(duel, DuelDto.class);
        try {
            duelDto.setTurns(objectMapper.readValue(duel.getTurns(), new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        duelDto.setFighters(duel.getHeroes().stream()
                .map(fighterBuilder::build)
                .toList());
        return duelDto;
    }

    public NotificationDto getResponseDto(Notification notification) {
        log.info("Конвертер переводит {} в {}", Notification.class, NotificationDto.class);
        return modelMapper.map(notification, NotificationDto.class);
    }
}
