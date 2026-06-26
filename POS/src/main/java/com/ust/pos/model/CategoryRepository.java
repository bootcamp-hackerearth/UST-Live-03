package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdentifier(String identifier);

    Category findByIdentifierAndDeletedFalse(String identifier);

    Page<Category> findByDeletedFalse(Pageable pageable);

    List<Category> findBySuperCategoryIsNotAndDeletedFalse(String category);

    List<Category> findByStatusAndDeletedFalse(boolean status);

    boolean existsBySuperCategoryAndDeletedFalse(String identifier);
}