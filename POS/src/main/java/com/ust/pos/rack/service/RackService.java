package com.ust.pos.rack.service;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

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
}
