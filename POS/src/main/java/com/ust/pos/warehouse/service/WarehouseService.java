package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WarehouseService {
    WarehouseDto save(WarehouseDto warehouseDto);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);

    List<WarehouseDto> findAllActive();

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
