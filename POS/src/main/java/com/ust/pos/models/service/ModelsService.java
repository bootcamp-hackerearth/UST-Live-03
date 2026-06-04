package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {
    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    void delete(String identifier);

    List<ModelsDto> findAll(Pageable pageable);

    ModelsDto findByIdentifier(String identifier);

    List<Models> findActiveModels();

    void toggleStatus(String identifier);
}
