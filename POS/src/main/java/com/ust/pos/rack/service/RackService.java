package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RackService {
    WsDto<RackDto> findAll(Pageable pageable);

    RackDto findByIdentifier(String identifier);

    RackDto save(RackDto shelfDto);

    RackDto update(RackDto shelfDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    List<RackDto> findActiveStatus();

    List<RackDto> findActiveRack();

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable);
}
