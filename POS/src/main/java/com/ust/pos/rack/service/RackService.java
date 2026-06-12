package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto findByIdentifier(String identifier);

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    WsDto<RackDto> findAll(Pageable pageable);

    List<RackDto> findAllActive();

}