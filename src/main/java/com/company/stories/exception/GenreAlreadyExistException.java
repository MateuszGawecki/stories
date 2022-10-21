package com.company.stories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Genre already exist")
public class GenreAlreadyExistException extends RuntimeException {
    public GenreAlreadyExistException(String message) {
        super(message);
    }
}
