package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.old.item.RequestItemPatternDto;
import ru.gizka.api.service.old.item.ItemPatternService;

@Component
@Slf4j
public class ItemPatternValidator implements Validator {
    private final ItemPatternService itemPatternService;

    @Autowired
    public ItemPatternValidator(ItemPatternService itemPatternService){
        this.itemPatternService = itemPatternService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return RequestItemPatternDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestItemPatternDto itemDto = (RequestItemPatternDto) target;
        log.info("Валидатор шаблона предметов проверяет новый предмет: {}", itemDto.getName());
        validateCreate(itemDto, errors);
    }

    private void validateCreate(RequestItemPatternDto itemDto, Errors errors) {
        if(itemPatternService.getByName(itemDto.getName()).isPresent()){
            errors.rejectValue("name", "", "Название шаблона предмета уже существует");
            log.info("Валидатор шаблона предметов сообщает, что название шаблон уже существует: {}", itemDto.getName());
        }

        if(itemDto.getName() == null || itemDto.getName().equals("null")){
            errors.rejectValue("name", "", "Недопустимое название");
            log.info("Валидатор шаблона предметов сообщает, что название недопустимо: {}", itemDto.getName());
        }
    }
}
