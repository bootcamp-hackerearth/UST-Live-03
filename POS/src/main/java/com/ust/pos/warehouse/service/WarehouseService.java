package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WarehouseService {

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto delete(String identifier);

    PaginatedResponseDto<WarehouseDto> findAll(Pageable pageable);

    List<WarehouseDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);
}