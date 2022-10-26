package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenreTest {
    private static final String genreName = "Comedy";
    private static final Long genreId = 1L;

    @Test
    void when_GenreBuilder_expect_Genre(){
        //given

        //when
        Genre genre = Genre.builder()
                .genre_id(genreId)
                .name(genreName)
                .build();

        //then
        assertEquals(genreId, genre.getGenre_id());
        assertEquals(genreName, genre.getName());
    }

    @Test
    void when_setGenreName_expect_newGenreNameInEntity() {
        //given
        String newName = "Drama";

        Genre genre = Genre.builder()
                .genre_id(genreId)
                .name(genreName)
                .build();


        //when
        genre.setName(newName);

        //then
        assertEquals(genreId, genre.getGenre_id());
        assertEquals(newName, genre.getName());
    }
}
