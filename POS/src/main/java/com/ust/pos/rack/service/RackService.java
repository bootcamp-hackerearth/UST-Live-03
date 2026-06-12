package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    WsDto<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    RackDto toggleStatus(String identifier);

    public List<RackDto> findActiveRacks();

}
