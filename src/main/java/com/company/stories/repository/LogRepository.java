package com.company.stories.repository;

import com.company.stories.model.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Override
    <S extends Log> S save(S entity);

    @Override
    Page<Log> findAll(Pageable pageable);

    Page<Log> findByLogMessageContainingIgnoreCase(String message, Pageable pageable);

    Page<Log> findByDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Log> findByLogMessageContainingIgnoreCaseAndDateBetween(String msg, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Log> findByLogMessageContainingIgnoreCaseAndDateAfter(String msg, LocalDateTime start, Pageable pageable);

    Page<Log> findByLogMessageContainingIgnoreCaseAndDateBefore(String msg, LocalDateTime end, Pageable pageable);

    Page<Log> findByDateAfter(LocalDateTime start, Pageable pageable);

    Page<Log> findByDateBefore(LocalDateTime end, Pageable pageable);
}
