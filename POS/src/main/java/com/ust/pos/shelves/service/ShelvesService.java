package com.ust.pos.shelves.service;

import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelves;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelvesService {
    ShelvesDto save(ShelvesDto shelvesDto);

    ShelvesDto update(ShelvesDto shelvesDto);

    void delete(String identifier);

    WsDto<ShelvesDto> findAll(Pageable pageable);

    ShelvesDto findByIdentifier(String identifier);

    List<Shelves> findActiveShelves();

    void toggleStatus(String identifier);

    WsDto<ShelvesDto> findAll(Specification<Shelves> example, Pageable pageable, String keyword);
    
}