package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    void delete(String identifier);
}
