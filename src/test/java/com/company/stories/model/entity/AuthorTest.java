package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorTest {
    private static final String authorName = "John";
    private static final String authorSurname = "Smith";
    private static final Long authorId = 1L;

    @Test
    void when_AuthorBuilder_expect_Author(){
        //given

        //when
        Author author = Author.builder()
                .author_id(authorId)
                .name(authorName)
                .surname(authorSurname)
                .build();

        //then
        assertEquals(authorId, author.getAuthor_id());
        assertEquals(authorName, author.getName());
        assertEquals(authorSurname, author.getSurname());
    }

    @Test
    void when_setAuthorNameAndSurname_expect_newNamesInEntity() {
        //given
        String newName = "Adam";
        String newSurname = "Atkinson";

        Author author = Author.builder()
                .author_id(authorId)
                .name(authorName)
                .surname(authorSurname)
                .build();


        //when
        author.setName(newName);
        author.setSurname(newSurname);

        //then
        assertEquals(authorId, author.getAuthor_id());
        assertEquals(newName, author.getName());
        assertEquals(newSurname, author.getSurname());
    }
}
