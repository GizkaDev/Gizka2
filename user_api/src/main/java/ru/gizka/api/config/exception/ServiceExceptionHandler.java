package ru.gizka.api.config.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.gizka.api.dto.ExceptionResponse;

import java.util.Arrays;

@ControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(ValidationException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(BadCredentialsException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(JWTVerificationException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ExceptionResponse> handleException(InternalAuthenticationServiceException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(JWTDecodeException e) {
        ExceptionResponse response = new ExceptionResponse(e.getClass().getName(), e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
