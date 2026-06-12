package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface RacksService {
    RacksDto save(RacksDto racksDto);

    RacksDto update(RacksDto racksDto);

    void delete(String username);

    WsDto<RacksDto> findAll(Pageable pageable);

    RacksDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
