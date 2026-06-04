package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {

    ModelsDto save(ModelsDto modelsDto);

    ModelsDto findById(Long id);

    List<ModelsDto> findAll(Pageable pageable);

    void deleteById(Long id);

    ModelsDto update(ModelsDto modelsDto);

    ModelsDto changeModelsStatus(String identifier, boolean status);

}
