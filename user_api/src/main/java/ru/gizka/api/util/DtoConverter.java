package ru.gizka.api.util;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.RequestHeroDto;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.dto.ResponseHeroDto;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.model.Hero;

@Component
@Slf4j
public class DtoConverter {
    private final ModelMapper modelMapper;

    public DtoConverter(){
        this.modelMapper = new ModelMapper();
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
        log.info("Конвертер переводит {} в {}",RequestHeroDto.class, Hero.class);
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
        return heroDto;
    }
}
