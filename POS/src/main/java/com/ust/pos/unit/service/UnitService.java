package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);

    List<UnitDto> findAll();

    List<UnitDto> findAll(Pageable pageable);

    void delete(String identifier);

    void toggleStatus(String identifier);
}