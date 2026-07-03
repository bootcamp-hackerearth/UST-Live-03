package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RacksService {
    WsDto<RacksDto> findAll(Pageable pageable);

    WsDto<RacksDto> findAll(Specification<Racks>example,Pageable pageable);

    RacksDto save(RacksDto racksDto);

    void delete(String identifier);

    RacksDto findByIdentifier(String identifier);

    RacksDto update(RacksDto racksDto);

    RacksDto changeToggleStatus(String identifier, boolean status);

    List<RacksDto> findActiveStatus();
}