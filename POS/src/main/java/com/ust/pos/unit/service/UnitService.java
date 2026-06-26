package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);

    List<UnitDto> findAll();

    Page<UnitDto> findAll(Pageable pageable, String search);

    void delete(String identifier);

    void toggleStatus(String identifier);
}