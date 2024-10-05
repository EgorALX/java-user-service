package ru.users.userservice.controller.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorMessage {

    private String message;

    private String status;

    public ErrorMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status.getReasonPhrase().toUpperCase();
    }
}
