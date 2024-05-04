package ru.gizka.api.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.gizka.api.dto.item.RequestItemDto;
import ru.gizka.api.service.item.ItemService;

@Component
@Slf4j
public class ItemValidator implements Validator {
    private final ItemService itemService;

    @Autowired
    public ItemValidator (ItemService itemService){
        this.itemService = itemService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return RequestItemDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestItemDto itemDto = (RequestItemDto) target;
        log.info("Валидатор предметов проверяет новый предмет: {}", itemDto.getName());
        validateCreate(itemDto, errors);
    }

    private void validateCreate(RequestItemDto itemDto, Errors errors) {
        if(itemService.getByName(itemDto.getName()).isPresent()){
            errors.rejectValue("name", "", "Название предмета уже существует");
            log.info("Валидатор предметов сообщает, что название предмета уже существует: {}", itemDto.getName());
        }

        if(itemDto.getName() == null || itemDto.getName().equals("null")){
            errors.rejectValue("name", "", "Недопустимое название");
            log.info("Валидатор предметов сообщает, что название недопустимо: {}", itemDto.getName());
        }
    }
}
