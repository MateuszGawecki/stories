package com.company.stories.repository;

import com.company.stories.model.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    @Override
    <S extends UserBook> S save(S entity);

    List<UserBook> findByUserId(Long userId);
}
