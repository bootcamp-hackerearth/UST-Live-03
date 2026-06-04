package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {
    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    List<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findActiveRacks();

    RackDto toggleStatus(String identifier, boolean status);
}