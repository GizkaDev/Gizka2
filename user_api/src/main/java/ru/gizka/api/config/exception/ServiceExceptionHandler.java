package ru.gizka.api.config.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.gizka.api.dto.ExceptionResponse;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class ServiceExceptionHandler {

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(Exception e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(ValidationException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(BadCredentialsException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(JWTVerificationException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(InternalAuthenticationServiceException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(JWTDecodeException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(HttpMessageNotReadableException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(HandlerMethodValidationException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(NoResourceFoundException e) {
        logException(e);
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private void logException(Exception e){
        log.error("""
                Перехвачено исключение: {},
                Сообщение: {}
                """, e.getClass().getName(), e.getMessage());
        e.printStackTrace();
    }
}
