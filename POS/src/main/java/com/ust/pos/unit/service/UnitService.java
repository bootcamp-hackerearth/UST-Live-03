package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {

    UnitDto save(UnitDto unitDto);

    UnitDto findById(Long id);

    List<UnitDto> findAll(Pageable pageable);

    void delete(Long id);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);
}
