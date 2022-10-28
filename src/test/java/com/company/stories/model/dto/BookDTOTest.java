package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookDTOTest {
    private static final Long bookId = 1L;
    private static final String title = "Book of books";
    private static final String description = "Lorem ipsum dolor";
    private static final String image_path = "BoB1233211";
    private static final Float global_score = 7.9F;
    private static final Integer votes = 1000;
    private static final Set<AuthorDTO> authors = new HashSet<>();
    private static final Set<GenreDTO> genres = new HashSet<>();

    @Test
    void when_BookDTOBuilder_expect_BookDTO(){
        //given

        //when
        BookDTO book = BookDTO.builder()
                .bookId(bookId)
                .title(title)
                .description(description)
                .imagePath(image_path)
                .globalScore(global_score)
                .votes(votes)
                .authors(authors)
                .genres(genres)
                .build();

        //then
        assertEquals(bookId, book.getBookId());
        assertEquals(title, book.getTitle());
        assertEquals(description, book.getDescription());
        assertEquals(image_path, book.getImagePath());
        assertEquals(votes, book.getVotes());
        assertEquals(global_score, book.getGlobalScore());
    }

    @Test
    void when_setBookDTOTitleAndDesc_expect_newValuesInDTO() {
        //given
        String newTitle = "Book2";
        String newDesc = "Lorem 2";

        BookDTO book = BookDTO.builder()
                .bookId(bookId)
                .title(title)
                .description(description)
                .imagePath(image_path)
                .globalScore(global_score)
                .votes(votes)
                .authors(authors)
                .genres(genres)
                .build();


        //when
        book.setTitle(newTitle);
        book.setDescription(newDesc);

        //then
        assertEquals(bookId, book.getBookId());
        assertEquals(newTitle, book.getTitle());
        assertEquals(newDesc, book.getDescription());
    }
}
