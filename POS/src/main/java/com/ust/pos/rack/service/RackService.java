package com.ust.pos.rack.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    RackDto save(RackDto rackDto);

    RackDto update(RackDto rackDto);

    void delete(String identifier);

    PaginatedResponseDto<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    List<RackDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
