package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    User findByIdentifier(String identifier);

    Page<User> findByDeletedFalse(Pageable pageable);

    Page<User> findAll(Specification<User> spec, Pageable pageable);
}