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

    boolean delete(String identifier);

    void toggleStatus(String identifier);

    List<WarehouseDto> findIfTrue();

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WsDto<WarehouseDto> findAll(Specification<Warehouse> spec, Pageable pageable, String keyword);
}