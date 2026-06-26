package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RacksService {
    RacksDto save(RacksDto brandDto);

    RacksDto update(RacksDto brandDto);

    void delete(String identifier);

    List<RacksDto> findAll();

    Page<RacksDto> findAll(Pageable pageable, String search);

    RacksDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
