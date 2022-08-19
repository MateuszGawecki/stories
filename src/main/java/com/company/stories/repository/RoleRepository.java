package com.company.stories.repository;

import com.company.stories.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Override
    List<Role> findAll();

    @Override
    <S extends Role> S save(S entity);
}
