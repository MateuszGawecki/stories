package com.company.stories.model.mapper;

import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public abstract class CommentMapper {

    public static Comment toCommentEntity(CommentDTO commentDTO){
        return Comment.builder()
                .comment_id(commentDTO.getCommentId())
                .comment(commentDTO.getComment())
                .isPublic(commentDTO.getIsPublic())
                .build();
    }

    public static CommentDTO toCommentDTO(Comment comment){
        return CommentDTO.builder()
                .commentId(comment.getComment_id())
                .comment(comment.getComment())
                .isPublic(comment.getIsPublic())
                .build();
    }
}
