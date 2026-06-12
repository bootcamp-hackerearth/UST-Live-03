package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto save(RackDto rackDto);

    WsDto<RackDto> findAll(Pageable pageable);

    RackDto update(RackDto rackDto);

    RackDto findByIdentifier(String identifier);

    void delete(String identifier);

    RackDto toggleStatus(String identifier, boolean status);

    List<RackDto> findActiveRacks();

}