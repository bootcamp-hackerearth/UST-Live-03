package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WarehouseService {
    WsDto<WarehouseDto> findAll(Pageable pageable);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);

    WarehouseDto save(WarehouseDto warehouseDto);

    void delete(String identifier);

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto changeToggleStatus(String identifier, boolean status);

    List<WarehouseDto> findActiveStatus();
}
