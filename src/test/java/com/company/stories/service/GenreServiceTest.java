package com.company.stories.service;

import com.company.stories.exception.genre.GenreNotFoundException;
import com.company.stories.model.entity.Genre;
import com.company.stories.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    GenreRepository genreRepository;
    @InjectMocks
    GenreService genreService;

    @Test
    public void when_findGenreByNameWithExistingGenreName_expectGenre(){
        //given
        String genreName = "fantasy";
        Genre existingGenre = Genre.builder()
                .genre_id(10000L)
                .name(genreName)
                .build();
        //when
        when(genreRepository.findByName(anyString())).thenReturn(Optional.ofNullable(existingGenre));
        //then
        Genre potentialGenre = genreService.findGenreByName(genreName);
        assertEquals(genreName, potentialGenre.getName());
    }

    @Test
    public void when_findGenreByNameWithNonExistingGenreName_expectGenreNotFoundException(){
        //given
        String genreName = "fantasy 007 James Bond Genre";
        //when
        when(genreRepository.findByName(anyString())).thenReturn(Optional.empty());
        //then
        GenreNotFoundException exception = assertThrows(GenreNotFoundException.class, () -> {
           genreService.findGenreByName(genreName);
        });
        assertEquals(String.format("Genre %s not found", genreName), exception.getMessage());
    }

    @Test
    public void when_createGenreWithNonAlreadyExistingName_expectNewGenre(){
        //given
        //when
        //then

        assertTrue(true);
    }

    @Test
    public void when_createGenreWithAlreadyExistingName_expectGenreAlreadyExistException(){
        //given
        //when
        //then

        assertTrue(true);
    }

    @Test
    public void when_deleteExistingGenre_expectVoid(){
        //given
        //when
        //then

        assertTrue(true);
    }

    @Test
    public void when_deleteNonExistingGenre_expectGenreNotFoundExeption(){
        //given
        //when
        //then

        assertTrue(true);
    }

    @Test
    public void when_updateExistingGenre_expectUpdatedGenre(){
        //given
        //when
        //then

        assertTrue(true);
    }

    @Test
    public void when_updateNonExistingGenre_expectGenreNotFoundException(){
        //given
        //when
        //then

        assertTrue(true);
    }
}
