package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    List<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
