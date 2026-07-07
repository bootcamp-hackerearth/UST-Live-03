package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDto> findAll();

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    WarehouseDto findByIdentifier(String identifier);

    void delete(String identifier);

    void toggleStatus(String identifier);

    Page<WarehouseDto> findAll(Example<Warehouse> example, Pageable pageable);
}
