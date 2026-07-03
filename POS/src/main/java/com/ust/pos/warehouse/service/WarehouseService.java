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

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> spec, Pageable pageable);

    WarehouseDto findById(Long id);

    void delete(String identifier);

    List<WarehouseDto> findAllActiveWarehouse();

    WarehouseDto changeWarehouseStatus(String identifier, boolean status);

    WarehouseDto findByIdentifier(String identifier);
}