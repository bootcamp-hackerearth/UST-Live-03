package com.ust.pos.unit.service;


import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    boolean delete(String identifier);

    PageDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<UnitDto> findActiveUnits();
}
