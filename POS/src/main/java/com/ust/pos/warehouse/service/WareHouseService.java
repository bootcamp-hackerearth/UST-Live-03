package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WareHouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WareHouseService {
    WareHouseDto save(WareHouseDto warehouseDto);

    WareHouseDto update(WareHouseDto warehouseDto);

    void delete(String identifier);

    List<WareHouseDto> findAll(Pageable pageable);

    WareHouseDto findByIdentifier(String identifier);

    WareHouseDto toggleStatus(String identifier, boolean status);

    List<WareHouseDto> findActiveWareHouse();
}