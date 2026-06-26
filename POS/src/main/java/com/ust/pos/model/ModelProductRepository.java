package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Long> {
    ModelProduct findByIdentifier(String identifier);

    ModelProduct findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<ModelProduct> findByIsDeleteFalse();

    Page<ModelProduct> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<ModelProduct> findByIsDeleteFalse(Pageable pageable);
}
