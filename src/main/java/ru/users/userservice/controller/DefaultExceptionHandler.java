package ru.users.userservice.controller;

import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.users.userservice.controller.exception.model.ErrorMessage;
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
    public ErrorMessage handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.info("Data is not valid {}", exception.getMessage());
        return new ErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(final NotFoundException exception) {
        log.info("Data not found {}", exception.getMessage());
        return new ErrorMessage(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.info(exception.getMessage());
        return new ErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(final ValidationException exception) {
        log.info("Validation error: {}", exception.getMessage());
        return new ErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(final Exception exception) {
        log.error("Exception: ", exception);
        return new ErrorMessage(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
