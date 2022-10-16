package com.company.stories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Friendship conflict")
public class CannotDeleteFriendshipException extends RuntimeException{

    public CannotDeleteFriendshipException(String message){
        super(message);
    }
}
