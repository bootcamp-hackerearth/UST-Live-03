package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    WarehouseDto save(WarehouseDto warehouseDto);

    PaginationResponseDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    void delete(String identifier);
}