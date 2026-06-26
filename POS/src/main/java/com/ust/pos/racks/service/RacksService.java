package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RacksService {
    RacksDto save(RacksDto racksDto);

    RacksDto update(RacksDto racksDto);

    void delete(String identifier);

    WsDto<RacksDto> findAll(Pageable pageable);

    RacksDto findByIdentifier(String identifier);

    List<RacksDto> findAllActive();

    void toggleStatus(String identifier);

}
