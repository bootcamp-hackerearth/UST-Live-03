package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RacksService {
    RacksDto save(RacksDto racksDto);

    RacksDto update(RacksDto racksDto);

    boolean delete(String identifier);

    List<RacksDto> findAll(Pageable pageable);

    RacksDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
