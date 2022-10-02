package com.company.stories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Author already exist")
public class AuthorAlreadyExistException extends RuntimeException{

    public AuthorAlreadyExistException(String message){
        super(message);
    }
}
