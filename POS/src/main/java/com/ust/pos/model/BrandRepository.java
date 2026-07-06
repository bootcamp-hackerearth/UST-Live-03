package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Brand> findByDeletedFalse(Pageable pageable);

    Page<Brand> findAll(Specification example, Pageable pageable);
}