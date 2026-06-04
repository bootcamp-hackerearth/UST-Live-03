package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RacksService {
    List<RacksDto> findAll(Pageable pageable);

    RacksDto save(RacksDto racksDto);

    void delete(String identifier);

    RacksDto findByIdentifier(String identifier);

    RacksDto update(RacksDto racksDto);

    RacksDto changeToggleStatus(String identifier, boolean status);
}