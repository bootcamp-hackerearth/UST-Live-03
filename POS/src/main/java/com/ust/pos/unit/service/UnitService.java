package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String username);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    UnitDto toggleStatus(String identifier, boolean status);

    List<UnitDto> findActiveUnit();

    WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable);
}