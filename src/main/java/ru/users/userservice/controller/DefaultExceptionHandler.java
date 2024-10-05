package ru.users.userservice.controller;

import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.users.userservice.controller.exception.model.errorMessage;
import ru.users.userservice.controller.exception.model.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public errorMessage handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.info("Data is not valid {}", exception.getMessage());
        return new errorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public errorMessage handleNotFoundException(final NotFoundException exception) {
        log.info("Data not found {}", exception.getMessage());
        return new errorMessage(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public errorMessage handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.info(exception.getMessage());
        return new errorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public errorMessage handleValidationException(final ValidationException exception) {
        log.info("Validation error: {}", exception.getMessage());
        return new errorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public errorMessage handleException(final Exception exception) {
        log.error("Exception: ", exception);
        return new errorMessage(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
