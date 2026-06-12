package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {

    WarehouseDto save(WarehouseDto warehouse);

    WarehouseDto update(WarehouseDto warehouse);

    void delete(String identifier);

    WsDto<WarehouseDto> findAll(Pageable pageable);

    WarehouseDto findByIdentifier(String identifier);

}
