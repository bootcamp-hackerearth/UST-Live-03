package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RackService {

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    RackDto findByIdentifier(String identifier);

    WsDto<RackDto> findAll(Pageable pageable);

    List<RackDto> getActiveRacks();

    void delete(String identifier);

    RackDto toggleStatus(String identifier);

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);
}