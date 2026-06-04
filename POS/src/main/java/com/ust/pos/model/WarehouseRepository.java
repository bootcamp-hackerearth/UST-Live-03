package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Warehouse findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
