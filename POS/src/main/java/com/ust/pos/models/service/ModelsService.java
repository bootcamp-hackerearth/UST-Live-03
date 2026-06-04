package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelsService {
    ModelsDto save(ModelsDto modelProductDto);

    ModelsDto update(ModelsDto modelProductDto);

    ModelsDto findByIdentifier(String identifier);

    List<ModelsDto> findAll(Pageable pageable);

    void delete(String identifier);

    void updateStatus(String identifier, boolean status);

    List<ModelsDto> findAllActive();
}