package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String indentifier);

    void deleteByIdentifier(String identifier);

    Page<Category> findByIdentifierContainingIgnoreCase(String identifier, Pageable pageable);
}