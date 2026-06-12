package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String identifier);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

}