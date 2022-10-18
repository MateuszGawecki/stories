package com.company.stories.repository;

import com.company.stories.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    <S extends Comment> S save(S entity);

    List<Comment> findByUserIdAndBookId(Long userId, Long bookId);

    List<Comment> findByUserId(Long userId);
}
