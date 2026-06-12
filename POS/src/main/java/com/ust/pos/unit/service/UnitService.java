package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface UnitService {

    UnitDto save(UnitDto unitDto);

    UnitDto findById(Long id);

    WsDto<UnitDto> findAll(Pageable pageable);

    void delete(Long id);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);
}
