package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Racks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RacksService {
    RacksDto findByIdentifier(String identifier);

    RacksDto save(RacksDto racksDto);

    RacksDto update(RacksDto racksDto);

    void delete(String identifier);

    WsDto<RacksDto> findAll(Pageable pageable);

    List<RacksDto> findAllActive();

    RacksDto toggleStatus(String identifier);

    WsDto<RacksDto> findAll(Specification<Racks> example, Pageable pageable);
}