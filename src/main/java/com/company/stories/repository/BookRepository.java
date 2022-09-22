package com.company.stories.repository;

import com.company.stories.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    <S extends Book> S save(S entity);

    @Override
    List<Book> findAll();
}
