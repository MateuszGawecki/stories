package com.company.stories.repository;

import com.company.stories.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    @Modifying
    @Query("delete from Comment c where c.comment_id = ?1")
    void deleteById(Long id);
}
