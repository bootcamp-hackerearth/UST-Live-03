package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    void delete(String identifier);

    List<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto findByIdentifier(String identifier);

    List<Warehouse> findActiveWarehouses();

    void toggleStatus(String identifier);
}
