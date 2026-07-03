package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    Page<Category> findByDeletedFalse(Pageable pageable);

    List<Category> findAllByStatusAndDeletedFalse(Boolean status);

    List<Category> findByStatusTrueAndDeletedFalseAndSuperCategoryIsNot(String empty);

    Page<Category> findAll(Specification<Category> example, Pageable pageable);
}
