package com.ust.pos.rack.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {
    PaginationResponseDto<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findActiveRacks();

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    RackDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
