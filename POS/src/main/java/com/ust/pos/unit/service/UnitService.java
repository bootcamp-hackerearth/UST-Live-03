package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto findByIdentifier(String identifier);

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    void delete(String identifier);

    WsDto<UnitDto> findAll(Pageable pageable);

    UnitDto changeToggleStatus(String identifier, boolean status);

    List<UnitDto> findActiveStatus();
}
