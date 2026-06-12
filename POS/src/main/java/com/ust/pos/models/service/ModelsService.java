package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {

    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    void delete(String identifier);

    WsDto<ModelsDto> findAll(Pageable pageable);

    ModelsDto findByIdentifier(String identifier);

    ModelsDto toggleStatus(String identifier);

    List<ModelsDto> findActiveModels();

}
