package com.company.stories.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    private static final String message = "Nice book";
    private static final Long commentId = 1L;

    @Test
    void when_CommentBuilder_expect_Comment(){
        //given

        //when
        Comment comment = Comment.builder()
                .comment_id(commentId)
                .comment(message)
                .build();

        //then
        assertEquals(commentId, comment.getComment_id());
        assertEquals(message, comment.getComment());
    }

    @Test
    void when_setComment_expect_newCommentInEntity() {
        //given
        String newComment = "Very nice book";

        Comment comment = Comment.builder()
                .comment_id(commentId)
                .comment(message)
                .build();


        //when
        comment.setComment(newComment);

        //then
        assertEquals(commentId, comment.getComment_id());
        assertEquals(newComment, comment.getComment());
    }
}
