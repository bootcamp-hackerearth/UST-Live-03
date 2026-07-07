package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Warehouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface WarehouseService {

    WarehouseDto findByIdentifier(String identifier);

    WarehouseDto save(WarehouseDto warehouseDto);

    WarehouseDto update(WarehouseDto warehouseDto);

    void delete(String identifier);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto toggleStatus(String identifier);

    List<WarehouseDto> findAllActive();

    WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable);

}
