package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {

    ModelsDto save(ModelsDto modelsDto);

    ModelsDto findById(Long id);

    WsDto<ModelsDto> findAll(Pageable pageable);

    void deleteById(Long id);

    ModelsDto update(ModelsDto modelsDto);

    ModelsDto changeModelsStatus(String identifier, boolean status);

    List<ModelsDto> findActiveModels();
}
