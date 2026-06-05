package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {
    ModelDto save(ModelDto modelDto);

    List<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ModelDto update(ModelDto modelDto);

    ModelDto toggleStatus(String identifier, boolean status);
}