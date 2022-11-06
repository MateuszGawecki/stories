package com.company.stories.repository;

import com.company.stories.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    <S extends Book> S save(S entity);

    @Override
    Page<Book> findAll(Pageable pageable);

    Optional<Book> findByTitle(String title);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByAuthorsNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Book> findByAuthorsNameContainingAndAuthorsSurnameContainingIgnoreCase(String name, String surname, Pageable pageable);

    Page<Book> findByGenresNameContainingIgnoreCase(String genre, Pageable pageable);
}
