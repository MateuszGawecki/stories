package com.company.stories.exception.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Book not exist")
public class BookNotExistException extends RuntimeException{

    public BookNotExistException(String message){
        super(message);
    }
}
