package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface UnitService {

    UnitDto save(UnitDto unitDto);

    UnitDto findById(Long id);

    WsDto<UnitDto> findAll(Pageable pageable);

    WsDto<UnitDto> findAll(Specification<Unit> spec, Pageable pageable);

    void delete(String identifier);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);
}