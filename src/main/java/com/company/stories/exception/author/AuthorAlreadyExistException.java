package com.company.stories.exception.author;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Author already exist")
public class AuthorAlreadyExistException extends RuntimeException{

    public AuthorAlreadyExistException(String message){
        super(message);
    }
}
