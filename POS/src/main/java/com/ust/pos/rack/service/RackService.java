package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RackService {

    RackDto findByIdentifier(String identifier);

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    WsDto<RackDto> findAll(Pageable pageable);

    List<RackDto> findAllActive();

    RackDto toggleStatus(String identifier);

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);

}