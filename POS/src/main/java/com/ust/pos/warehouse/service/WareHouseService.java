package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.WareHouseDto;
import org.springframework.data.domain.Pageable;



public interface WareHouseService {
    WareHouseDto save(WareHouseDto wareHouseDto);

    WareHouseDto update(WareHouseDto wareHouseDto);

    boolean delete(String identifier);

    PageDto<WareHouseDto> findAll(Pageable pageable);

    WareHouseDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
