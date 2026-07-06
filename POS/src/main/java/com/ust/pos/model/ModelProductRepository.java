package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Long> {

    ModelProduct findByIdentifier(String identifier);

    Page<ModelProduct> findByIsDeletedFalse(Pageable pageable);

    void deleteByIdentifier(String identifier);

    List<ModelProduct> findByStatusTrueAndIsDeletedFalse();

    Page<ModelProduct> findAll(Specification example, Pageable pageable);
}
