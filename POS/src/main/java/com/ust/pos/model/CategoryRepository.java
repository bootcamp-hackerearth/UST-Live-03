package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    List<Category> findBySupercategoryIsNot(String supercategory);

    List<Category> findByStatusTrue();

    Page<Category> findByDeletedFalse(Pageable pageable);
}
