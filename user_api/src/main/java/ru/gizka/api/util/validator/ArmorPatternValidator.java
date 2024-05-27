package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.old.item.armor.RequestArmorPatternDto;
import ru.gizka.api.service.old.item.armor.ArmorPatternService;

@Component
@Slf4j
public class ArmorPatternValidator implements Validator {
    private final ArmorPatternService armorPatternService;

    @Autowired
    public ArmorPatternValidator(ArmorPatternService armorPatternService){
        this.armorPatternService = armorPatternService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestArmorPatternDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestArmorPatternDto armorDto = (RequestArmorPatternDto) target;
        log.info("Валидатор шаблона доспехов проверяет новый доспех: {}", armorDto.getName());
        validateCreate(armorDto, errors);
    }

    private void validateCreate(RequestArmorPatternDto armorDto, Errors errors) {
        if(armorPatternService.getByName(armorDto.getName()).isPresent()){
            errors.rejectValue("name", "", "Название шаблона доспеха уже существует");
            log.info("Валидатор шаблона доспехов сообщает, что название шаблона уже существует: {}", armorDto.getName());
        }

        if(armorDto.getName() == null || armorDto.getName().equals("null")){
            errors.rejectValue("name", "", "Недопустимое название");
            log.info("Валидатор шаблона доспехов сообщает, что название недопустимо: {}", armorDto.getName());
        }
    }
}
