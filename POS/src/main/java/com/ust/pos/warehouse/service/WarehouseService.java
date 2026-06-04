package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDto> findAll(Pageable pageable);

    List<WarehouseDto> findByStatusTrue();

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
