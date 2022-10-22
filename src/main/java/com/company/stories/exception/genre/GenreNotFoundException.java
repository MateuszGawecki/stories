package com.company.stories.exception.genre;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Genre not found")
public class GenreNotFoundException extends RuntimeException{

    public GenreNotFoundException(String message){
        super(message);
    }
}
