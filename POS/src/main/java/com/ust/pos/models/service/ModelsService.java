package com.ust.pos.models.service;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ModelsService {
    ModelsDto save(ModelsDto modelsDto);

    WsDto<ModelsDto> findAll(Pageable pageable);

    WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable);

    List<ModelsDto> findAllActive();

    ModelsDto findByIdentifier(String identifier);

    ModelsDto update(ModelsDto modelsDto);

    ModelsDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
