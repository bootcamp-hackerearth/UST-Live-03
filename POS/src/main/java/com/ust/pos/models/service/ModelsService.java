package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {
    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    ModelsDto findByIdentifier(String identifier);

    WsDto<ModelsDto> findAll(Pageable pageable);

    void delete(String identifier);

    void updateStatus(String identifier, boolean status);

    List<ModelsDto> findAllActive();
}