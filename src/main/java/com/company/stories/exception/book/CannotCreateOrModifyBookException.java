package com.company.stories.exception.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cannot create or modify book")
public class CannotCreateOrModifyBookException extends RuntimeException{

    public CannotCreateOrModifyBookException(String message){
        super(message);
    }
}
