package com.company.stories.service;

import com.company.stories.exception.CommentNotExistException;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.mapper.CommentMapper;
import com.company.stories.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getCommentsForUser(Long userId){
        List<Comment> userComments = commentRepository.findByUserId(userId);

        List<CommentDTO> commentDTOS = userComments.stream().map(CommentMapper::toCommentDTO).collect(Collectors.toList());

        return commentDTOS;
    }

    public CommentDTO addCommentForUserAndBook(Long userId, Long bookId, String comment) {
        Comment newComment = Comment.builder()
                .userId(userId)
                .bookId(bookId)
                .comment(comment)
                .build();

        Comment dbComment = commentRepository.save(newComment);
        return CommentMapper.toCommentDTO(dbComment);
    }

    public Comment getComment(Long comment_id) {
        Optional<Comment> dbComment = commentRepository.findById(comment_id);

        if(dbComment.isEmpty())
            throw new CommentNotExistException(String.format("Comment with id %d not exist", comment_id));

        return dbComment.get();
    }

    public CommentDTO editComment(Comment dbComment, CommentDTO commentDTO) {
        dbComment.setComment(commentDTO.getComment());

        return CommentMapper.toCommentDTO(commentRepository.save(dbComment));
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
