package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RackService {

    List<RackDto> findAll(Pageable pageable);

    RackDto save(RackDto dto);

    RackDto findByIdentifier(String identifier);

    RackDto update(RackDto dto);

    void delete(String identifier);

    void toggleStatus(String identifier);
}
