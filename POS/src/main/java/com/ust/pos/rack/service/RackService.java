package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RackService {
    WsDto<RackDto> findAll(Pageable pageable);

    RackDto save(RackDto dto);

    RackDto findByIdentifier(String identifier);

    RackDto update(RackDto dto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable, String keyword);

}
