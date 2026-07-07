package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Long> , JpaSpecificationExecutor<ModelProduct> {
    ModelProduct findByIdentifier(String identifier);

    ModelProduct findByIdentifierAndDeletedFalse(String identifier);

    Page<ModelProduct> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier,
            Pageable pageable
    );

    Page<ModelProduct> findByDeletedFalse(Pageable pageable);

    List<ModelProduct> findByDeletedFalse();
}
