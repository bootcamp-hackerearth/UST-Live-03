package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface RacksService {

    RacksDto save(RacksDto racksDto);

    RacksDto findById(Long id);

    WsDto<RacksDto> findAll(Pageable pageable);

    WsDto<RacksDto> findAll(Specification<Racks> spec, Pageable pageable);

    void delete(String identifier);

    RacksDto findByIdentifier(String identifier);

    RacksDto changeRacksStatus(String identifier, boolean status);

    RacksDto update(RacksDto racksDto);
}