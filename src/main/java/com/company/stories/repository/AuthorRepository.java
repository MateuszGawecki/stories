package com.company.stories.repository;

import com.company.stories.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Override
    <S extends Author> S save(S entity);

    Optional<Author> findByNameAndSurname(String name, String surname);
}
