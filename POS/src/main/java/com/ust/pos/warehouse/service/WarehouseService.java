package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    void delete(String identifier);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto findByIdentifier(String identifier);

    List<WarehouseDto> findActiveWarehouse();

    void toggleStatus(String identifier);
}
