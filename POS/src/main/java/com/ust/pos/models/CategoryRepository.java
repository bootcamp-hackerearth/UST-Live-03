package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdentifier(String identifier);

    List<Category> findBySuperCategoryIsNotNull();

    boolean existsBySuperCategoryAndDeletedFalse(String superCategory);

    List<Category> findByStatusTrueAndDeletedFalse();

    Category findByIdentifierAndDeletedFalse (String identifier);

    Page<Category> findAllByDeletedFalse (Pageable pageable);

    Page<Category> findAll(Specification <Category> example, Pageable pageable);

}


