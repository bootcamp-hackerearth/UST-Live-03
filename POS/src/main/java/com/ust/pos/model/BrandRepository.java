package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByIdentifier(String identifier);

    Brand findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Brand> findByIsDeleteFalse();

    Page<Brand> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Brand> findByIsDeleteFalse(Pageable pageable);

}
