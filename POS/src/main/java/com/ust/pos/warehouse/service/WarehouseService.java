package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    PaginationResponseDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    void delete(String identifier);

    PaginationResponseDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);
}