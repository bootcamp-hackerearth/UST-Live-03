package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Long> {
    ModelProduct findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ModelProduct findByIdentifierAndDeletedFalse(String identifier);

    List<ModelProduct> findByDeletedFalse();

    Page<ModelProduct> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<ModelProduct> findByDeletedFalse(Pageable pageable);
}
