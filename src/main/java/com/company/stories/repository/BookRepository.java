package com.company.stories.repository;

import com.company.stories.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    <S extends Book> S save(S entity);

    @Override
    List<Book> findAll();

    Optional<Book> findByTitle(String title);
}
