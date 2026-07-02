package com.ust.pos.unit.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Transactional
public interface UnitService {
    UnitDto save(UnitDto unitDto);

    UnitDto update(UnitDto unitDto);

    boolean delete(String identifier);

    PageDto<UnitDto> findAll(Pageable pageable);

    UnitDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<UnitDto> findActiveUnits();

    PageDto<UnitDto> findAll(Specification<Unit> spec, Pageable pageable, String keyword);
}
