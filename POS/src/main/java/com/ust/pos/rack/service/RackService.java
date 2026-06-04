package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {
    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    List<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<Rack> findActiveRacks();

    void toggleStatus(String identifier);

}
