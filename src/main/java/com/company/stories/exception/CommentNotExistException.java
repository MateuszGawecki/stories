package com.company.stories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Comment not exist")
public class CommentNotExistException extends RuntimeException{

    public CommentNotExistException(String message) {
        super(message);
    }
}
