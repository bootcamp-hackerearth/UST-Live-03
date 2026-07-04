package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface RackService {

    RackDto createRack(RackDto rackDto);

    RackDto updateRack(RackDto rackDto);

    RackDto getRack(Long id);

    boolean deleteRack(Long id);

    WsDto<RackDto> findAll(Pageable pageable);

    WsDto<RackDto> findAll(Specification<Rack> spec, Pageable pageable, String keyword);}