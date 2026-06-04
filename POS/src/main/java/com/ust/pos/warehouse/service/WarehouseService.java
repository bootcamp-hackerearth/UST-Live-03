package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto save(WarehouseDto warehouseDto);

    void delete(String identifier);

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto changeToggleStatus(String identifier, boolean status);

    List<WarehouseDto> findActiveStatus();
}
