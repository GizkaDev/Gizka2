package ru.gizka.api.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.model.AppUser;

@Component
public class DtoConverter {
    private final ModelMapper modelMapper;

    public DtoConverter(){
        this.modelMapper = new ModelMapper();
    }

    public AppUser getModel(RequestAppUserDto userDto) {
        return AppUser.builder()
                .login(userDto.getLogin())
                .password(userDto.getPassword())
                .build();
    }

    public ResponseAppUserDto getResponseDto(AppUser appUser) {
        return modelMapper.map(appUser, ResponseAppUserDto.class);
    }
}
