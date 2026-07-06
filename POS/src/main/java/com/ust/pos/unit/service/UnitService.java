package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UnitService {

    UnitDto save(UnitDto unitDto);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);

    void delete(String identifier);

    UnitDto toggleStatus(String identifier, boolean status);

    WsDto<UnitDto> findAll(Specification<Unit> specification, Pageable pageable);

}