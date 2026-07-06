package com.ust.pos.rack.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RackService {
    PaginationResponseDto<RackDto> findAll(Pageable pageable);

    PaginationResponseDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findActiveRacks();

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    RackDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
