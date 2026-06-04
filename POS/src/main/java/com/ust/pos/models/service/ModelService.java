package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    void delete(String identifier);

    List<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    List<ModelDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
