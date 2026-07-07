package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UnitService {
    UnitDto findByIdentifier(String identifier);

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String identifier);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto toggleStatus(String identifier);

    WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable);
}