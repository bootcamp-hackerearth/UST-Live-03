package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long>,JpaSpecificationExecutor<Category> {
    Brand findByIdentifier(String identifier);

    Page<Brand> findByDeletedFalse(Pageable pageable);

    List<Brand> findByStatusTrue();

    Page<Brand> findAll(Specification spec, Pageable pageable);
}
