package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Category> findAllByStatus(Boolean status);

    List<Category> findByStatusTrueAndSuperCategoryIsNot(String empty);
}
