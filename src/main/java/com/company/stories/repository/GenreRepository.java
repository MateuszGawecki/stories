package com.company.stories.repository;

import com.company.stories.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Override
    <S extends Genre> S save(S entity);

    Optional<Genre> findByName(String name);
}
