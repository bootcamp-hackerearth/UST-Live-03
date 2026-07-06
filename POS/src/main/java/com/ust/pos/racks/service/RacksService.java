package com.ust.pos.racks.service;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
public interface RacksService {

    RacksDto save(RacksDto racksDto);
    RacksDto update(RacksDto racksDto);
    boolean delete(String identifier);
    WsDto<RacksDto> findAll(Pageable pageable);
    RacksDto findByIdentifier(String identifier);
    RacksDto toggleStatus(String identifier);
    List<RacksDto> findIfTrue();
    WsDto<RacksDto> findAll(Specification<Racks> example, Pageable pageable, String keyword);

}