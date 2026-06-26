package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    UnitDto findByIdentifier(String identifier);

    List<UnitDto> findAll();

    WsDto<UnitDto> findAll(Pageable pageable);

    void delete(String identifier);

    Page<UnitDto> findAll(String search, Pageable pageable);

    void toggleStatus(String identifier);
}