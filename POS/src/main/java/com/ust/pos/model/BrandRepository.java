package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
// 1. MAKE SURE THIS EXACT IMPORT IS PRESENT:
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Brand> findByDeletedFalse(Pageable pageable);

    Brand findByIdentifierAndDeletedFalse(String identifier);

    Page<Brand> findAll(Specification<Brand> example, Pageable pageable);

}