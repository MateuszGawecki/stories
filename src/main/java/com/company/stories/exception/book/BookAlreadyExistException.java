package com.company.stories.exception.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Book already exist")
public class BookAlreadyExistException extends RuntimeException{

    public BookAlreadyExistException(String message){
        super(message);
    }
}
