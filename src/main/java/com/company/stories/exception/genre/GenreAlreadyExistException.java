package com.company.stories.exception.genre;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Genre already exist")
public class GenreAlreadyExistException extends RuntimeException {
    public GenreAlreadyExistException(String message) {
        super(message);
    }
}
