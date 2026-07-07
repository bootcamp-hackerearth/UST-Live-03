package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RacksService {
    RacksDto save(RacksDto brandDto);
    RacksDto update(RacksDto brandDto);
    void delete(String identifier);
    List<RacksDto> findAll();
    RacksDto findByIdentifier(String identifier);
    WsDto<RacksDto> findAll(Pageable pageable);
    Page<RacksDto> findAll(Example<Racks> example, Pageable pageable);
    void toggleStatus(String identifier);
}
