package com.company.stories.model.mapper;

import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public abstract class CommentMapper {

    public static Comment toCommentEntity(CommentDTO commentDTO){
        return Comment.builder()
                .comment_id(commentDTO.getComment_id())
                .userId(commentDTO.getUserId())
                .bookId(commentDTO.getBookId())
                .comment(commentDTO.getComment())
                .build();
    }

    public static CommentDTO toCommentDTO(Comment comment){
        return CommentDTO.builder()
                .comment_id(comment.getComment_id())
                .userId(comment.getUserId())
                .bookId(comment.getBookId())
                .comment(comment.getComment())
                .build();
    }
}
