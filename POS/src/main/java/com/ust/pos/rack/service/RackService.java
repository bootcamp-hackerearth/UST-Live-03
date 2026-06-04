package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto createRack(RackDto rackDto);

    RackDto updateRack(RackDto rackDto);

    RackDto getRack(Long id);

    List<RackDto> findAll(Pageable pageable);

    boolean deleteRack(Long id);
}