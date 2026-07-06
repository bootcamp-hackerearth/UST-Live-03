package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface WarehouseService {
    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto findByIdentifier(String warehouseCode);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);
}
