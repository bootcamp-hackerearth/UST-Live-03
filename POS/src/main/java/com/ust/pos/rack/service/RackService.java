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

    void delete(String identifier);

    WsDto<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findActiveRacks();

    RackDto toggleStatus(String identifier, boolean status);

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);
}