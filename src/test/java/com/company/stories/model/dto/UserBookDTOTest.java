package com.company.stories.model.dto;

import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.entity.UserBook;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserBookDTOTest {
    private static final Long userToBookId = 1L;
    private static final Long userId = 2L;
    private static final Long bookId = 3L;
    private static final String bookTitle = "Book1";
    private static final BookDTO book = BookDTO.builder().bookId(bookId).title(bookTitle).build();
    private static final Integer userRating = 9;
    private static final List<CommentDTO> comments = new ArrayList<>();

    @Test
    void when_UserBookDTOBuilder_expect_UserBookDTO(){
        //given

        //when
        UserBookDTO ub = UserBookDTO.builder()
                .userBookId(userToBookId)
                .userId(userId)
                .bookDTO(book)
                .userRating(userRating)
                .commentDTOs(comments)
                .build();

        //then
        assertEquals(userToBookId, ub.getUserBookId());
        assertEquals(userId, ub.getUserId());
        assertEquals(book, ub.getBookDTO());
        assertEquals(userRating, ub.getUserRating());
    }

    @Test
    void when_setUserRatingAndComments_expect_newValuesInDTO() {
        //given
        CommentDTO newComment = CommentDTO.builder().commentId(10L).comment("new comment").build();
        Integer newUserRating = 4;

        UserBookDTO ub = UserBookDTO.builder()
                .userBookId(userToBookId)
                .userId(userId)
                .bookDTO(book)
                .userRating(userRating)
                .commentDTOs(comments)
                .build();


        //when
        ub.setUserRating(newUserRating);
        ub.getCommentDTOs().add(newComment);

        //then
        assertEquals(userToBookId, ub.getUserBookId());
        assertEquals(userId, ub.getUserId());
        assertEquals(book, ub.getBookDTO());
        assertEquals(newUserRating, ub.getUserRating());
        assertTrue(ub.getCommentDTOs().contains(newComment));
    }
}
