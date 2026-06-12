package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String identifier);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    List<Unit> findActiveUnits();

    UnitDto toggleStatus(String identifier);
}
