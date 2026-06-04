package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findAll(Pageable pageable);

    List<RackDto> getActiveRacks();

    void delete(String identifier);

    RackDto toggleStatus(String identifier);
}