package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    void delete(String identifier);

    List<WarehouseDto> findActiveWarehouses();

    WarehouseDto toggleStatus(String identifier);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);
}