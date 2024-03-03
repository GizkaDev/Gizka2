package ru.gizka.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gizka.api.dto.RequestAppUserDto;
import ru.gizka.api.dto.ResponseAppUserDto;
import ru.gizka.api.facade.AuthFacade;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthFacade authFacade;

    @Autowired
    public AuthController (AuthFacade authFacade){
        this.authFacade = authFacade;
    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseAppUserDto> create(@Valid @RequestBody RequestAppUserDto userDto,
                                                     BindingResult bindingResult)  {
        ResponseAppUserDto responseUser = authFacade.create(userDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody RequestAppUserDto userDto) {
        return ResponseEntity.ok(authFacade.getToken(userDto));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Hello, world!");
    }
}
