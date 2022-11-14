package com.company.stories.repository;

import com.company.stories.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();

    @Override
    <S extends User> S save(S entity);

    @Override
    Page<User> findAll(Pageable pageable);

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Set<User> findByNameContainingAndSurnameContainingIgnoreCase(String name, String surname);

    Set<User> findByNameContainingIgnoreCase(String name);

    Page<User> findByNameContainingAndSurnameContainingIgnoreCase(String name, String surname, Pageable pageable);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
