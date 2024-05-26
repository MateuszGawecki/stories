package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDTOTest {
    private static final Long userId = 1L;
    private static final String name = "John";
    private static final String surname = "Smith";
    private static final String email = "email@example.com";

    @Test
    void when_UserDTOBuilder_expect_UserDTO(){
        //given

        //when
        UserDTO userDTO = UserDTO.builder()
                .userId(userId)
                .name(name)
                .surname(surname)
                .email(email)
                .build();

        //then
        assertEquals(userId, userDTO.getUserId());
        assertEquals(name, userDTO.getName());
        assertEquals(surname, userDTO.getSurname());
    }

    @Test
    void when_setUserNameAndSurname_expect_newNamesInDTO() {
        //given
        String newName = "Adam";
        String newSurname = "Atkinson";

        UserDTO userDTO = UserDTO.builder()
                .userId(userId)
                .name(name)
                .surname(surname)
                .email(email)
                .build();


        //when
        userDTO.setName(newName);
        userDTO.setSurname(newSurname);

        //then
        assertEquals(userId, userDTO.getUserId());
        assertEquals(newName, userDTO.getName());
        assertEquals(newSurname, userDTO.getSurname());
    }
}
