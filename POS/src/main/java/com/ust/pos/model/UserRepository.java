package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    void deleteByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findByDeletedFalse(Pageable pageable);

    Page<User> findAll(Specification<User> spec, Pageable pageable);
}