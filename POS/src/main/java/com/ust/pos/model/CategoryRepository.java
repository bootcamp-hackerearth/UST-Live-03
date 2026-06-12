package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Category> findBySuperCategoryIsNot(String category);

    List<Category> findByStatus(boolean status);

    boolean existsBySuperCategory(String identifier);

}