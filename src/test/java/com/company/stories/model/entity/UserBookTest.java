package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserBookTest {
    private static final Long userToBookId = 1L;
    private static final Long userId = 2L;
    private static final Long bookId = 3L;
    private static final String bookTitle = "Book1";
    private static final Book book = Book.builder().book_id(bookId).title(bookTitle).build();
    private static final Integer userRating = 9;
    private static final List<Comment> comments = new ArrayList<>();

    @Test
    void when_UserBookBuilder_expect_UserBookEntity(){
        //given

        //when
        UserBook ub = UserBook.builder()
                .user_to_book_id(userToBookId)
                .userId(userId)
                .book(book)
                .userRating(userRating)
                .comments(comments)
                .build();

        //then
        assertEquals(userToBookId, ub.getUser_to_book_id());
        assertEquals(userId, ub.getUserId());
        assertEquals(book, ub.getBook());
        assertEquals(userRating, ub.getUserRating());
    }

    @Test
    void when_setUserRatingAndComments_expect_newValuesInEntity() {
        //given
        Comment newComment = Comment.builder().comment_id(10L).comment("new comment").build();
        Integer newUserRating = 4;

        UserBook ub = UserBook.builder()
                .user_to_book_id(userToBookId)
                .userId(userId)
                .book(book)
                .userRating(userRating)
                .comments(comments)
                .build();


        //when
        ub.setUserRating(newUserRating);
        ub.getComments().add(newComment);

        //then
        assertEquals(userToBookId, ub.getUser_to_book_id());
        assertEquals(userId, ub.getUserId());
        assertEquals(book, ub.getBook());
        assertEquals(newUserRating, ub.getUserRating());
        assertTrue(ub.getComments().contains(newComment));
    }
}
