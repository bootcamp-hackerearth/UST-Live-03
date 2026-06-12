package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ModelsService {
    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    void delete(String identifier);

    WsDto<ModelsDto> findAll(Pageable pageable);

    ModelsDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
