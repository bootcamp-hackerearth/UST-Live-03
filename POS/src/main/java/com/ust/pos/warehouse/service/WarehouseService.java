package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    void delete(String identifier);

    List<WarehouseDto> findActiveWarehouses();

    WarehouseDto toggleStatus(String identifier);
}