package com.company.stories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Friendship conflict")
public class CannotCreateFriendshipException extends RuntimeException{

    public CannotCreateFriendshipException(String message){
        super(message);
    }
}
