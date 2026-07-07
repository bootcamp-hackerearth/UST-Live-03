package com.ust.pos.unit.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UnitService {

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto delete(String identifier);

    PaginatedResponseDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    List<UnitDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable);
}