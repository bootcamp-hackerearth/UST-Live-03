package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    void delete(String identifier);

    List<WarehouseDto> findAll(Pageable pageable);

    List<WarehouseDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
