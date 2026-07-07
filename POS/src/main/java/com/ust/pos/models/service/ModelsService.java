package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ModelsService {

    ModelsDto save(ModelsDto modelsDto);

    ModelsDto update(ModelsDto modelsDto);

    ModelsDto findByIdentifier(String identifier);

    WsDto<ModelsDto> findAll(Pageable pageable);

    void delete(String identifier);

    ModelsDto toggleStatus(String identifier);

    List<ModelsDto> findActiveModels();

    WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable);
}