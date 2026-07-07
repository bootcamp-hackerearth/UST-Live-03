package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameAndDeletedFalse(String username);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    Page<User> findAll(Specification example, Pageable pageable);
}
