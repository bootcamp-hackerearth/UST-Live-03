package com.ust.pos.shelves.service;

import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.model.Shelves;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelvesService {
    ShelvesDto save(ShelvesDto shelvesDto);

    ShelvesDto update(ShelvesDto shelvesDto);

    void delete(String identifier);

    List<ShelvesDto> findAll(Pageable pageable);

    ShelvesDto findByIdentifier(String identifier);

    List<Shelves> findActiveShelves();

    void toggleStatus(String identifier);
}