package com.ust.pos.model.service;

import com.ust.pos.dto.ModelDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {
    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    void delete(String identifier);

    List<ModelDto> findAll(Pageable pageable);

    List<ModelDto> findAllActive();

    void toggleStatus(String identifier);
}
