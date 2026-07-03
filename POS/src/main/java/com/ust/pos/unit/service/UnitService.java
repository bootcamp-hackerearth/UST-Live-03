package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    WsDto<UnitDto> findAll(Pageable pageable);

    WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable);

    List<UnitDto> findAllActive();

    UnitDto findByIdentifier(String identifier);

    UnitDto update(UnitDto unitDto);

    UnitDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
