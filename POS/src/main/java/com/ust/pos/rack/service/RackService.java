package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {
    List<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    List<RackDto> findActiveStatus();

    List<RackDto> findActiveRack();
}
