package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RackService {
    RackDto save(RackDto rackDto);

    WsDto<RackDto> findAll(Pageable pageable);

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);

    List<RackDto> findAllActive();

    RackDto findByIdentifier(String identifier);

    RackDto update(RackDto rackDto);

    RackDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
