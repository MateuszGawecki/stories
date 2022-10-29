package com.company.stories.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Friendship not found")
public class FriendshipNotFoundException extends RuntimeException{

    public FriendshipNotFoundException(String message){
        super(message);
    }
}
