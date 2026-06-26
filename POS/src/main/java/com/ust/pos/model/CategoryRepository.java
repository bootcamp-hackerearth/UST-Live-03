package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    List<Category> findByStatusTrue();

    void deleteByIdentifier(String identifier);

    List<Category> findBySuperCategoryIsNotNullAndStatusTrue();

    Page<Category> findByIsDeletedFalse(Pageable pageable);
}
