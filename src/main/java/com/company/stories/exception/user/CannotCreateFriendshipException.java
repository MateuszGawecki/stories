package com.company.stories.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Friendship conflict")
public class CannotCreateFriendshipException extends RuntimeException{

    public CannotCreateFriendshipException(String message){
        super(message);
    }
}
