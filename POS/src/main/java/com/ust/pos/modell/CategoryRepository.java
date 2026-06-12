package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Category> findBySuperCategoryIsNotNull();

    boolean existsBySuperCategory(String superCategory);

    List<Category> findByStatusTrue();
}

