package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<User> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Page<User> findAll(Specification<User> example, Pageable pageable);
}