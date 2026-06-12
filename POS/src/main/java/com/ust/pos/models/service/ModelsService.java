package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface
ModelsService {

    ModelsDto save(ModelsDto modelsDto);
    ModelsDto update(ModelsDto modelsDto);
    boolean delete(String identifier);
    List<ModelsDto> findAll(Pageable pageable);
    ModelsDto findByIdentifier(String identifier);
    ModelsDto toggleStatus(String identifier);
    List<ModelsDto> findIfTrue();

}