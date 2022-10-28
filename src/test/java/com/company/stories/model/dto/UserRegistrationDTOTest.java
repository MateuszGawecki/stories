package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRegistrationDTOTest {
    private static final String name = "John";
    private static final String surname = "Smith";
    private static final String email = "email@example.com";
    private static final String password = "1234";

    @Test
    void when_UserRegistrationDTOBuilder_expect_DTO(){
        //given
        UserDTO userDTO = UserDTO.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .build();

        //when
        UserRegistrationDTO urDTO = UserRegistrationDTO.builder()
                .userDTO(userDTO)
                .password(password)
                .build();

        //then
        assertEquals(password, urDTO.getPassword());
        assertEquals(userDTO, urDTO.getUserDTO());
    }
}
