package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    void deleteByIdentifier(String identifier);

    Warehouse findByIdentifier(String identifier);

    List<Warehouse> findByStatusTrue();
}
