package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    Page<Category> findByDeletedFalse(Pageable pageable);

    List<Category> findByDeletedFalse();

    Page<Category> findAll(Specification <Category>example, Pageable pageable);

}