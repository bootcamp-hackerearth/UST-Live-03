package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    List<UnitDto> findAll(Pageable pageable);

    List<UnitDto> findAllActive();

    UnitDto findByIdentifier(String identifier);

    UnitDto update(UnitDto unitDto);

    UnitDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
