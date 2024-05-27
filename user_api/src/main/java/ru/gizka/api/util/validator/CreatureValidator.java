package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.old.creature.RequestCreatureDto;
import ru.gizka.api.service.old.CreatureService;

@Component
@Slf4j
public class CreatureValidator implements Validator {

    private final CreatureService creatureService;

    @Autowired
    public CreatureValidator(CreatureService creatureService) {
        this.creatureService = creatureService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestCreatureDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestCreatureDto creatureDto = (RequestCreatureDto) target;
        validateCreate(creatureDto, errors);
    }

    private void validateCreate(RequestCreatureDto creatureDto, Errors errors) {
        if (creatureService.getByNameOptional(creatureDto.getName()).isPresent()) {
            errors.rejectValue("name", "", "Моб с таким названием уже существует");
            log.info("Валидатор мобов сообщает, что название занято: {}", creatureDto.getName());
        }

        if (creatureDto.getName() == null || creatureDto.getName().equals("null")) {
            errors.rejectValue("name", "", "Недопустимое название");
            log.info("Валидатор мобов сообщает, что название недопустимо: {}", creatureDto.getName());
        }
    }
}
