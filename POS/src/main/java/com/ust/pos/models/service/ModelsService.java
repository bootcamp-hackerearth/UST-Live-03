package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {

    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    void delete(String identifier);

    List<ModelsDto> findAll(Pageable pageable);

    ModelsDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<ModelsDto> findActiveModels();

}
