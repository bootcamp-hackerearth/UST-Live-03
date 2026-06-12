package com.ust.pos.unit.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;

public interface UnitService {
    PaginationResponseDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
