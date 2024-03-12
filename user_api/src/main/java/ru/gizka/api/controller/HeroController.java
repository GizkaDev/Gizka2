package ru.gizka.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gizka.api.dto.RequestHeroDto;
import ru.gizka.api.dto.ResponseHeroDto;
import ru.gizka.api.facade.HeroFacade;
import ru.gizka.api.model.AuthUser;

@RestController
@Slf4j
@RequestMapping("/api/user/hero")
public class HeroController {
    private final HeroFacade heroFacade;

    @Autowired
    public HeroController(HeroFacade heroFacade){
        this.heroFacade = heroFacade;
    }

    @PostMapping
    public ResponseEntity<ResponseHeroDto> create(@AuthenticationPrincipal AuthUser authUser,
                                                  @Valid @RequestBody RequestHeroDto heroDto,
                                                  BindingResult bindingResult){
        log.info("Контроллер героев принял запрос POST /hero на создание героя: {} для пользователя: {}", heroDto, authUser.login());
        ResponseHeroDto response = heroFacade.create(authUser.getUser(), heroDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
