package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByIdentifier(String brand);

    void deleteByIdentifier(String identifier);

    List<Brand> findByStatusTrueAndIsDeletedFalse();
    Page<Brand> findByIsDeletedFalse(Pageable pageable);
    Page<Brand> findAll(Specification<Brand> example, Pageable pageable);

    Brand findByIdentifierAndIsDeletedFalse(String identifier);
}
