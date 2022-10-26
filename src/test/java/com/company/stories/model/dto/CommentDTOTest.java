package com.company.stories.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentDTOTest {
    private static final String message = "Nice book";
    private static final Long commentId = 1L;

    @Test
    void when_CommentDTOBuilder_expect_CommentDTO(){
        //given

        //when
        CommentDTO commentDTO = CommentDTO.builder()
                .commentId(commentId)
                .comment(message)
                .build();

        //then
        assertEquals(commentId, commentDTO.getCommentId());
        assertEquals(message, commentDTO.getComment());
    }

    @Test
    void when_setComment_expect_newCommentInDTO() {
        //given
        String newComment = "Very nice book";

        CommentDTO commentDTO = CommentDTO.builder()
                .commentId(commentId)
                .comment(message)
                .build();


        //when
        commentDTO.setComment(newComment);

        //then
        assertEquals(commentId, commentDTO.getCommentId());
        assertEquals(newComment, commentDTO.getComment());
    }
}
