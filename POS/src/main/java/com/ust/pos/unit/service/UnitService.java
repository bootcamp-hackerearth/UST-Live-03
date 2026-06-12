package com.ust.pos.unit.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    PaginationResponseDto<UnitDto> findAll(Pageable pageable);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);

    void delete(String identifier);

    UnitDto toggleStatus(String identifier, boolean status);
}