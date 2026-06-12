package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface RackService {
    WsDto<RackDto> findAll(Pageable pageable);

    RackDto save(RackDto dto);

    RackDto findByIdentifier(String identifier);

    RackDto update(RackDto dto);

    void delete(String identifier);

    void toggleStatus(String identifier);
}
