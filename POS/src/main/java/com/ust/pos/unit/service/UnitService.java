package com.ust.pos.unit.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UnitService {
    PaginationResponseDto<UnitDto> findAll(Pageable pageable);

    PaginationResponseDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
