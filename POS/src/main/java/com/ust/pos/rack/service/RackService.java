package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {
    RackDto save(RackDto rackDto);

    List<RackDto> findAll(Pageable pageable);

    List<RackDto> findAllActive();

    RackDto findByIdentifier(String identifier);

    RackDto update(RackDto rackDto);

    RackDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
