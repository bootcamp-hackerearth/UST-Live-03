package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    Brand findByIdentifier(String identifier);

    Brand findByIdentifierAndDeletedFalse(String identifier);

    Page<Brand> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String identifier, Pageable pageable);

    List<Brand> findByDeletedFalse();

    Page<Brand> findByDeletedFalse(Pageable pageable);
}