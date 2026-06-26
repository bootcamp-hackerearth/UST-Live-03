package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RacksService {
    RacksDto save(RacksDto racksDto);

    RacksDto update(RacksDto racksDto);

    boolean delete(String identifier);

    WsDto<RacksDto> findAll(Pageable pageable);

    RacksDto findByIdentifier(String identifier);

    RacksDto toggleStatus(String identifier);

    List<RacksDto> findIfTrue();
}