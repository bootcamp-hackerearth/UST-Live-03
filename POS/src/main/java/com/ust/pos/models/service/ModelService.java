package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {
    List<ModelDto> findAll(Pageable pageable);

    List<ModelDto> findByStatusTrue();

    ModelDto findByIdentifier(String identifier);

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    ModelDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
