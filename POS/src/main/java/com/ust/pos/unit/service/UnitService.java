package com.ust.pos.unit.service;

import com.ust.pos.dto.UnitDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UnitService {

    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    boolean delete(String identifier);

    List<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    UnitDto toggleStatus(String identifier);

    List<UnitDto> findIfTrue();

}
