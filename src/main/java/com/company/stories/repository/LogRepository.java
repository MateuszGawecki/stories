package com.company.stories.repository;

import com.company.stories.model.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Override
    <S extends Log> S save(S entity);
}
