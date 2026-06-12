package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    void delete(String identifier);

}