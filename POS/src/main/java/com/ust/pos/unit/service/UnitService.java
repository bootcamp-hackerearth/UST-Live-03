package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String identifier);

    List<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
