package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Brand> findByDeletedFalse();

    Page<Brand> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<Brand> findByDeletedFalse(Pageable pageable);

    Brand findByIdentifierAndDeletedFalse(String identifier);

}
