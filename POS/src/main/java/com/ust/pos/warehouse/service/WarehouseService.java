package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    List<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto findById(Long id);

    void delete(Long id);

    List<WarehouseDto> findAllActiveWarehouse();

    WarehouseDto changeWarehouseStatus(String identifier, boolean status);

    WarehouseDto findByIdentifier(String identifier);

}
