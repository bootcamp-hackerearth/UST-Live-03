package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    WarehouseDto save(WarehouseDto warehouseDto);

    List<WarehouseDto> findAll(Pageable pageable);

    List<WarehouseDto> findAllActive();

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
