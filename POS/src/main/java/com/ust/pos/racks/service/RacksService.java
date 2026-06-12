package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface RacksService {

    RacksDto save(RacksDto racksDto);

    RacksDto findById(Long id);

    WsDto<RacksDto> findAll(Pageable pageable);

    void deleteById(Long id);

    RacksDto findByIdentifier(String identifier);

    RacksDto changeRacksStatus(String identifier, boolean status);

    RacksDto update(RacksDto racksDto);
}
