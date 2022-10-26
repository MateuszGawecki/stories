package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorDTOTest {
    private static final String authorName = "John";
    private static final String authorSurname = "Smith";
    private static final Long authorId = 1L;

    @Test
    void when_AuthorDTOBuilder_expect_AuthorDTO(){
        //given

        //when
        AuthorDTO authorDTO = AuthorDTO.builder()
                .authorId(authorId)
                .authorName(authorName)
                .authorSurname(authorSurname)
                .build();

        //then
        assertEquals(authorId, authorDTO.getAuthorId());
        assertEquals(authorName, authorDTO.getAuthorName());
        assertEquals(authorSurname, authorDTO.getAuthorSurname());
    }

    @Test
    void when_setAuthorNameAndSurname_expect_newNamesInEntity() {
        //given
        String newName = "Adam";
        String newSurname = "Atkinson";

        AuthorDTO authorDTO = AuthorDTO.builder()
                .authorId(authorId)
                .authorName(authorName)
                .authorSurname(authorSurname)
                .build();


        //when
        authorDTO.setAuthorName(newName);
        authorDTO.setAuthorSurname(newSurname);

        //then
        assertEquals(authorId, authorDTO.getAuthorId());
        assertEquals(newName, authorDTO.getAuthorName());
        assertEquals(newSurname, authorDTO.getAuthorSurname());
    }
}
