package com.company.stories.model.dto;

import com.company.stories.model.entity.Genre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenreDTOTest {
    private static final String genreName = "Comedy";
    private static final Long genreId = 1L;

    @Test
    void when_GenreBuilder_expect_GenreDTO(){
        //given

        //when
        GenreDTO genreDTO = GenreDTO.builder()
                .genreId(genreId)
                .name(genreName)
                .build();

        //then
        assertEquals(genreId, genreDTO.getGenreId());
        assertEquals(genreName, genreDTO.getName());
    }

    @Test
    void when_setGenreName_expect_newGenreNameInDTO() {
        //given
        String newName = "Drama";

        GenreDTO genreDTO = GenreDTO.builder()
                .genreId(genreId)
                .name(genreName)
                .build();


        //when
        genreDTO.setName(newName);

        //then
        assertEquals(genreId, genreDTO.getGenreId());
        assertEquals(newName, genreDTO.getName());
    }
}
