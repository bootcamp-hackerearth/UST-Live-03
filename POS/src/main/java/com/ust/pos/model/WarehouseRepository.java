package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> , JpaSpecificationExecutor<Warehouse> {
    Warehouse findByIdentifier(String identifier);

    Warehouse findByIdentifierAndDeletedFalse(String identifier);

    Page<Warehouse> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier,
            Pageable pageable
    );

    Page<Warehouse> findByDeletedFalse(Pageable pageable);

    List<Warehouse> findByDeletedFalse();
}
