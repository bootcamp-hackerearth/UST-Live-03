package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
