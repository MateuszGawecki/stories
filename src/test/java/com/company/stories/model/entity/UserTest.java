package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private static final Long userId = 1L;
    private static final String name = "John";
    private static final String surname = "Smith";
    private static final String email = "email@example.com";

    @Test
    void when_UserBuilder_expect_UserEntity(){
        //given

        //when
        User user = User.builder()
                .user_id(userId)
                .name(name)
                .surname(surname)
                .email(email)
                .build();

        //then
        assertEquals(userId, user.getUser_id());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
    }

    @Test
    void when_setUserNameAndSurname_expect_newNamesInEntity() {
        //given
        String newName = "Adam";
        String newSurname = "Atkinson";

        User user = User.builder()
                .user_id(userId)
                .name(name)
                .surname(surname)
                .email(email)
                .build();


        //when
        user.setName(newName);
        user.setSurname(newSurname);

        //then
        assertEquals(userId, user.getUser_id());
        assertEquals(newName, user.getName());
        assertEquals(newSurname, user.getSurname());
    }
}
