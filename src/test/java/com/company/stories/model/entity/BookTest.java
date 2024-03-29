package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookTest {
    private static final Long bookId = 1L;
    private static final String title = "Book of books";
    private static final String description = "Lorem ipsum dolor";
    private static final String image_path = "BoB1233211";
    private static final Float global_score = 7.9F;
    private static final Integer votes = 1000;
    private static final Set<Author> authors = new HashSet<>();
    private static final Set<Genre> genres = new HashSet<>();

    @Test
    void when_BookBuilder_expect_BookEntity(){
        //given

        //when
        Book book = Book.builder()
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
    void when_setBookTitleAndDesc_expect_newValuesInEntity() {
        //given
        String newTitle = "Book2";
        String newDesc = "Lorem 2";

        Book book = Book.builder()
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
