package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RacksService {

    RacksDto save(RacksDto racksDto);

    RacksDto findById(Long id);

    List<RacksDto> findAll(Pageable pageable);

    void deleteById(Long id);

    RacksDto findByIdentifier(String identifier);

    RacksDto changeRacksStatus(String identifier, boolean status);

    RacksDto update(RacksDto racksDto);
}
