package com.company.stories.repository;

import com.company.stories.model.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    @Override
    <S extends UserBook> S save(S entity);

    @Override
    Optional<UserBook> findById(Long aLong);

    List<UserBook> findByUserId(Long userId);
}
