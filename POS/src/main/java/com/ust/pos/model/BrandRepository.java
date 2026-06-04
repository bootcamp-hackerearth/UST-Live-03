package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
