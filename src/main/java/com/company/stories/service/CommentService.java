package com.company.stories.service;

import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.mapper.CommentMapper;
import com.company.stories.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
