package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand,Long>, QueryByExampleExecutor<Brand> {
    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Brand> findByDeletedFalse();

    Page<Brand> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<Brand> findByDeletedFalse(Pageable pageable);

    Brand findByIdentifierAndDeletedFalse(String identifier);

}
