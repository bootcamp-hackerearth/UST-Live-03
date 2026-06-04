package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    List<WarehouseDto> findAll(Pageable pageable);

    void delete(String identifier);
}