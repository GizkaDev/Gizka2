package ru.gizka.api.util;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.model.AppUser;

@Component
@Slf4j
public class DtoConverter {
    private final ModelMapper modelMapper;

    public DtoConverter(){
        this.modelMapper = new ModelMapper();
    }

    public AppUser getModel(RequestAppUserDto userDto) {
        log.info("Конвертер переводит {} в {} для пользователя {}", RequestAppUserDto.class, AppUser.class, userDto.getLogin());
        return AppUser.builder()
                .login(userDto.getLogin())
                .password(userDto.getPassword())
                .build();
    }

    public ResponseAppUserDto getResponseDto(AppUser appUser) {
        log.info("Конвертер переводит {} в {} для пользователя {}", AppUser.class, ResponseAppUserDto.class, appUser.getLogin());
        return modelMapper.map(appUser, ResponseAppUserDto.class);
    }
}
